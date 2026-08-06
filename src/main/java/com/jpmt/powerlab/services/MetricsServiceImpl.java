package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.metrics.*;
import com.jpmt.powerlab.repositories.TrainingSetTemplateRepository;
import com.jpmt.powerlab.repositories.WorkoutSessionRepository;
import com.jpmt.powerlab.repositories.WorkoutSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MetricsServiceImpl implements MetricsService {

    private static final List<MainPattern> COMP_LIFTS = List.of(MainPattern.SQUAT, MainPattern.BENCH, MainPattern.DEADLIFT);
    private static final LocalDate EPOCH = LocalDate.of(1970, 1, 1);
    private static final int REP_MAX_UPPER_BOUND = 10;
    private static final int CONSISTENCY_WINDOW_WEEKS = 4;

    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final TrainingSetTemplateRepository setTemplateRepository;
    private final MetricsCalculator metricsCalculator;
    private final OneRepMaxProvider oneRepMaxProvider;
    private final BodyweightProvider bodyweightProvider;
    private final CompLiftProvider compLiftProvider;
    private final SexProvider sexProvider;

    public MetricsServiceImpl(WorkoutSetRepository workoutSetRepository,
                               WorkoutSessionRepository workoutSessionRepository,
                               TrainingSetTemplateRepository setTemplateRepository,
                               MetricsCalculator metricsCalculator,
                               OneRepMaxProvider oneRepMaxProvider,
                               BodyweightProvider bodyweightProvider,
                               CompLiftProvider compLiftProvider,
                               SexProvider sexProvider) {
        this.workoutSetRepository = workoutSetRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.setTemplateRepository = setTemplateRepository;
        this.metricsCalculator = metricsCalculator;
        this.oneRepMaxProvider = oneRepMaxProvider;
        this.bodyweightProvider = bodyweightProvider;
        this.compLiftProvider = compLiftProvider;
        this.sexProvider = sexProvider;
    }

    // ============================================================================
    // SERIES
    // ============================================================================

    @Override
    public MetricsResponse series(MetricsQuery query) {
        List<WorkoutSet> sets = workoutSetRepository.findForMetrics(
                query.from(), query.to(), query.exerciseIds(), query.patterns(),
                query.setFilter().setTypes());

        // Precarga de sets de plantilla para el tonelaje planificado.
        // Se recogen los IDs de sesión-plantilla presentes en el resultado y se carga
        // en una sola query, evitando N+1.
        Set<Long> sessionTemplateIds = sets.stream()
                .map(ws -> ws.getWorkoutEntry().getSession().getTrainingSessionTemplate())
                .filter(Objects::nonNull)
                .map(t -> t.getId())
                .collect(Collectors.toSet());

        Map<Long, List<TrainingSetTemplate>> setTemplatesBySessionTemplate = sessionTemplateIds.isEmpty()
                ? Map.of()
                : setTemplateRepository.findBySessionTemplateIdIn(sessionTemplateIds).stream()
                        .collect(Collectors.groupingBy(tst -> tst.getSessionTemplate().getId()));

        Map<String, List<WorkoutSet>> byGroupKey = sets.stream()
                .collect(Collectors.groupingBy(ws -> groupKey(ws, query.groupBy()), LinkedHashMap::new, Collectors.toList()));

        // Cache de refMax por patrón (no por ejercicio): todas las variantes de BENCH comparten
        // el 1RM del press de competición. Se resuelve vía CompLiftProvider.
        Map<MainPattern, Optional<Double>> patternRefMaxCache = new HashMap<>();

        List<MetricsSeries> series = byGroupKey.entrySet().stream()
                .map(e -> buildSeries(e.getKey(), e.getValue(), query,
                        patternRefMaxCache, setTemplatesBySessionTemplate))
                .toList();

        return new MetricsResponse(query.athleteId(), query.groupBy(), query.period(), query.from(), query.to(), series);
    }

    private String groupKey(WorkoutSet set, MetricsGroupBy groupBy) {
        Exercise exercise = set.getWorkoutEntry().getExercise();
        return switch (groupBy) {
            case EXERCISE -> String.valueOf(exercise.getId());
            case PATTERN -> exercise.getMainPattern().name();
            case NONE -> "all";
        };
    }

    private MetricsSeries buildSeries(String key, List<WorkoutSet> sets, MetricsQuery query,
                                       Map<MainPattern, Optional<Double>> patternRefMaxCache,
                                       Map<Long, List<TrainingSetTemplate>> setTemplatesBySessionTemplate) {
        Exercise firstExercise = sets.get(0).getWorkoutEntry().getExercise();
        String label = switch (query.groupBy()) {
            case EXERCISE -> firstExercise.getBaseName();
            case PATTERN -> firstExercise.getMainPattern().name();
            case NONE -> "Todos los ejercicios";
        };

        Map<String, List<WorkoutSet>> byPeriod = new LinkedHashMap<>();
        Map<String, LocalDate> periodMinDate = new HashMap<>();
        for (WorkoutSet ws : sets) {
            LocalDate date = ws.getWorkoutEntry().getSession().getDate();
            String periodKey = periodKey(ws.getWorkoutEntry().getSession(), date, query.period());
            byPeriod.computeIfAbsent(periodKey, k -> new ArrayList<>()).add(ws);
            periodMinDate.merge(periodKey, date, (d1, d2) -> d1.isBefore(d2) ? d1 : d2);
        }

        // Patrón para resolución de refMax (null cuando groupBy=NONE para no mezclar patrones)
        MainPattern refPattern = query.groupBy() == MetricsGroupBy.NONE
                ? null : firstExercise.getMainPattern();

        List<MetricsPoint> points = byPeriod.entrySet().stream()
                .sorted(Comparator.comparing(e -> periodMinDate.get(e.getKey())))
                .map(e -> buildPoint(query.period() == MetricsPeriod.NONE ? null : e.getKey(),
                        e.getValue(), query.athleteId(), refPattern, patternRefMaxCache,
                        query.groupBy(), setTemplatesBySessionTemplate))
                .toList();

        boolean estimated = points.stream().anyMatch(MetricsPoint::e1rmEstimated);
        MeasurementType measure = dominantMeasurementType(sets);

        return new MetricsSeries(key, label, measure, estimated, points);
    }

    private MeasurementType dominantMeasurementType(List<WorkoutSet> sets) {
        var types = sets.stream()
                .map(ws -> ws.getWorkoutEntry().getExercise().getMeasurementType())
                .map(mt -> mt == null ? MeasurementType.REPS : mt)
                .collect(Collectors.toSet());
        return types.size() == 1 ? types.iterator().next() : MeasurementType.REPS;
    }

    private String periodKey(WorkoutSession session, LocalDate date, MetricsPeriod period) {
        return switch (period) {
            case NONE -> "all";
            case DAY -> date.toString();
            case WEEK -> "%d-W%02d".formatted(
                    date.get(IsoFields.WEEK_BASED_YEAR), date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            case MONTH -> YearMonth.from(date).toString();
            case YEAR -> String.valueOf(date.getYear());
            case BLOCK -> session.getTrainingSessionTemplate() != null
                    ? session.getTrainingSessionTemplate().getBlockName()
                    : "Sin bloque";
        };
    }

    private MetricsPoint buildPoint(String periodLabel, List<WorkoutSet> sets, Long athleteId,
                                     MainPattern refPattern,
                                     Map<MainPattern, Optional<Double>> patternRefMaxCache,
                                     MetricsGroupBy groupBy,
                                     Map<Long, List<TrainingSetTemplate>> setTemplatesBySessionTemplate) {
        int setsCount = sets.size();
        int quantity = sets.stream().mapToInt(ws -> ws.getReps() == null ? 0 : ws.getReps()).sum();

        List<Double> tonnageContribs = new ArrayList<>();
        List<Double> loads = new ArrayList<>();
        List<Double> e1rms = new ArrayList<>();
        List<Double> rpes = new ArrayList<>();
        int hardSets = 0;
        int failedSets = 0;
        boolean e1rmEstimated = false;

        // Acumuladores para IMR ponderado por reps: Σ(load×reps) / Σ(reps) / refMax
        double imrWeightedSum = 0;
        int imrTotalReps = 0;
        Optional<Double> refMax = refPattern != null
                ? patternRefMaxCache.computeIfAbsent(refPattern, p ->
                        compLiftProvider.compLiftExerciseId(athleteId, p)
                                .flatMap(compId -> oneRepMaxProvider.referenceMax(athleteId, compId)))
                : Optional.empty();

        Double compRm = refMax.filter(rm -> rm > 0).orElse(null);

        for (WorkoutSet ws : sets) {
            Exercise exercise = ws.getWorkoutEntry().getExercise();
            Double load = metricsCalculator.effectiveLoad(ws, exercise, athleteId);
            if (load != null) {
                loads.add(load);
                MeasurementType mt = exercise.getMeasurementType() == null ? MeasurementType.REPS : exercise.getMeasurementType();
                if (mt == MeasurementType.REPS && ws.getReps() != null) {
                    tonnageContribs.add(load * ws.getReps());
                    if (compRm != null) {
                        imrWeightedSum += load * ws.getReps();
                        imrTotalReps += ws.getReps();
                    }
                }
            }

            if (metricsCalculator.isHardSet(ws)) hardSets++;
            if (metricsCalculator.isFailedSet(ws)) failedSets++;

            Double e1rm = metricsCalculator.e1rm(ws, exercise, athleteId);
            if (e1rm != null) {
                e1rms.add(e1rm);
                if (metricsCalculator.isEstimated(ws, exercise) || ws.getRpeValue() == null) {
                    e1rmEstimated = true;
                }
            }

            if (ws.getRpeValue() != null) rpes.add(ws.getRpeValue());
        }

        Double tonnage = tonnageContribs.isEmpty() ? null : round1(sum(tonnageContribs));
        Double avgLoad = average(loads);
        Double peakLoad = loads.isEmpty() ? null : round1(Collections.max(loads));
        Double bestE1rm = e1rms.isEmpty() ? null : round1(Collections.max(e1rms));
        Double avgE1rm = average(e1rms);
        Double avgRpe = average(rpes);
        Double avgIntensityPct = (imrTotalReps == 0 || compRm == null)
                ? null
                : round1(imrWeightedSum / imrTotalReps / compRm * 100);

        Double plannedTonnage = computePlannedTonnage(sets, groupBy, setTemplatesBySessionTemplate);

        return new MetricsPoint(periodLabel, setsCount, quantity, tonnage, plannedTonnage, avgLoad, peakLoad,
                hardSets, failedSets, bestE1rm, avgE1rm, e1rmEstimated, avgRpe, avgIntensityPct);
    }

    private Double computePlannedTonnage(List<WorkoutSet> sets, MetricsGroupBy groupBy,
                                          Map<Long, List<TrainingSetTemplate>> setTemplatesBySessionTemplate) {
        if (setTemplatesBySessionTemplate.isEmpty()) return null;

        // Clave de filtro por ejercicio o patrón para alinear el planificado con el ejecutado
        Set<Long> exerciseIds = groupBy == MetricsGroupBy.EXERCISE
                ? sets.stream().map(ws -> ws.getWorkoutEntry().getExercise().getId()).collect(Collectors.toSet())
                : null;
        Set<MainPattern> patterns = groupBy == MetricsGroupBy.PATTERN
                ? sets.stream().map(ws -> ws.getWorkoutEntry().getExercise().getMainPattern())
                        .filter(Objects::nonNull).collect(Collectors.toSet())
                : null;

        // IDs únicos de sesión-plantilla en este bucket de periodo
        Set<Long> sessionTemplateIds = sets.stream()
                .map(ws -> ws.getWorkoutEntry().getSession().getTrainingSessionTemplate())
                .filter(Objects::nonNull)
                .map(t -> t.getId())
                .collect(Collectors.toSet());

        double total = 0;
        boolean anyComputable = false;

        for (Long stId : sessionTemplateIds) {
            List<TrainingSetTemplate> templates = setTemplatesBySessionTemplate.getOrDefault(stId, List.of());
            for (TrainingSetTemplate tst : templates) {
                if (tst.getExercise() == null || tst.getTargetWeight() == null) continue;
                if (exerciseIds != null && !exerciseIds.contains(tst.getExercise().getId())) continue;
                if (patterns != null && !patterns.contains(tst.getExercise().getMainPattern())) continue;
                if (tst.getTargetSets() == null) continue;

                // Usar la media del rango de reps; si solo hay min, usar min
                double avgReps = tst.getTargetRepsMin() != null && tst.getTargetRepsMax() != null
                        ? (tst.getTargetRepsMin() + tst.getTargetRepsMax()) / 2.0
                        : tst.getTargetRepsMin() != null ? tst.getTargetRepsMin() : 0;
                if (avgReps <= 0) continue;

                total += tst.getTargetSets() * avgReps * tst.getTargetWeight();
                anyComputable = true;
            }
        }

        return anyComputable ? round1(total) : null;
    }

    // ============================================================================
    // PERSONAL RECORDS
    // ============================================================================

    @Override
    public PersonalRecordsResponse personalRecords(LocalDate from, LocalDate to,
                                                     List<Long> exerciseIds, List<MainPattern> patterns) {
        boolean hasExerciseIds = exerciseIds != null && !exerciseIds.isEmpty();
        boolean hasPatterns = patterns != null && !patterns.isEmpty();
        if (hasExerciseIds && hasPatterns) {
            throw new BadRequestException("Filtra por exerciseIds o por patterns, no por ambos a la vez.");
        }

        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        LocalDate resolvedFrom = from != null ? from : EPOCH;
        if (resolvedFrom.isAfter(resolvedTo)) {
            throw new BadRequestException("El parámetro 'from' no puede ser posterior a 'to'.");
        }

        Long athleteId = MetricsQuery.DEFAULT_ATHLETE_ID;
        List<Long> resolvedExerciseIds = exerciseIds;
        List<MainPattern> resolvedPatterns = patterns;
        if (!hasExerciseIds && !hasPatterns) {
            resolvedExerciseIds = defaultCompLiftExerciseIds(athleteId);
        }

        List<WorkoutSet> sets = workoutSetRepository.findForMetrics(
                resolvedFrom, resolvedTo, resolvedExerciseIds, resolvedPatterns,
                SetFilter.WORKING_SETS.setTypes());

        Map<Long, List<WorkoutSet>> byExercise = sets.stream()
                .collect(Collectors.groupingBy(ws -> ws.getWorkoutEntry().getExercise().getId(),
                        LinkedHashMap::new, Collectors.toList()));

        List<ExercisePrs> exercises = byExercise.entrySet().stream()
                .map(e -> buildExercisePrs(e.getKey(), e.getValue(), athleteId))
                .toList();

        return new PersonalRecordsResponse(athleteId, resolvedFrom, resolvedTo, exercises);
    }

    private List<Long> defaultCompLiftExerciseIds(Long athleteId) {
        return COMP_LIFTS.stream()
                .map(lift -> resolveLift(athleteId, lift).exerciseId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private ExercisePrs buildExercisePrs(Long exerciseId, List<WorkoutSet> sets, Long athleteId) {
        Exercise exercise = sets.get(0).getWorkoutEntry().getExercise();

        Comparator<WorkoutSet> byEarliestDate = Comparator.comparing(
                (WorkoutSet ws) -> ws.getWorkoutEntry().getSession().getDate(), Comparator.reverseOrder());

        PrSetInfo heaviestSet = sets.stream()
                .filter(ws -> ws.getWeight() != null)
                .max(Comparator.comparingDouble(WorkoutSet::getWeight)
                        .thenComparingInt(ws -> ws.getReps() == null ? 0 : ws.getReps())
                        .thenComparing(byEarliestDate))
                .map(this::toPrSetInfo)
                .orElse(null);

        Double bestE1rm = null;
        WorkoutSet bestE1rmSetRaw = null;
        for (WorkoutSet ws : sets) {
            Double e1rm = metricsCalculator.e1rm(ws, exercise, athleteId);
            if (e1rm != null && (bestE1rm == null || e1rm > bestE1rm)) {
                bestE1rm = e1rm;
                bestE1rmSetRaw = ws;
            }
        }
        Double roundedBestE1rm = bestE1rm == null ? null : round1(bestE1rm);
        PrSetInfo bestE1rmSet = bestE1rmSetRaw == null ? null : toPrSetInfo(bestE1rmSetRaw);

        List<RepMax> repMaxes = new ArrayList<>();
        for (int reps = 1; reps <= REP_MAX_UPPER_BOUND; reps++) {
            int repsToMatch = reps;
            sets.stream()
                    .filter(ws -> ws.getWeight() != null && ws.getReps() != null && ws.getReps() == repsToMatch)
                    .max(Comparator.comparingDouble(WorkoutSet::getWeight).thenComparing(byEarliestDate))
                    .ifPresent(ws -> repMaxes.add(new RepMax(repsToMatch, round1(ws.getWeight()),
                            ws.getWorkoutEntry().getSession().getDate())));
        }

        return new ExercisePrs(exerciseId, exercise.getBaseName(), heaviestSet, roundedBestE1rm, bestE1rmSet, repMaxes);
    }

    private PrSetInfo toPrSetInfo(WorkoutSet ws) {
        return new PrSetInfo(round1(ws.getWeight()), ws.getReps(), ws.getRpeValue(),
                ws.getWorkoutEntry().getSession().getDate());
    }

    // ============================================================================
    // SUMMARY
    // ============================================================================

    @Override
    public MetricsSummaryResponse summary(LocalDate adherenceFrom, LocalDate adherenceTo) {
        Long athleteId = MetricsQuery.DEFAULT_ATHLETE_ID;
        LocalDate today = LocalDate.now();
        LocalDate resolvedTo = adherenceTo != null ? adherenceTo : today;
        LocalDate resolvedFrom = adherenceFrom != null ? adherenceFrom : resolvedTo.minusDays(28);
        if (resolvedFrom.isAfter(resolvedTo)) {
            throw new BadRequestException("El parámetro 'adherenceFrom' no puede ser posterior a 'adherenceTo'.");
        }

        SbdSummary sbd = buildSbdSummary(athleteId, today);
        ConsistencySummary consistency = buildConsistencySummary(resolvedFrom, resolvedTo, today);
        TonnageSummary tonnage = buildTonnageSummary(athleteId, today);

        return new MetricsSummaryResponse(athleteId, sbd, consistency, tonnage);
    }

    private record ResolvedLift(Long exerciseId, boolean configured) {}

    private ResolvedLift resolveLift(Long athleteId, MainPattern lift) {
        Optional<Long> configured = compLiftProvider.compLiftExerciseId(athleteId, lift);
        if (configured.isPresent()) {
            return new ResolvedLift(configured.get(), true);
        }
        return new ResolvedLift(bestExerciseIdForPattern(lift), false);
    }

    private Long bestExerciseIdForPattern(MainPattern lift) {
        List<WorkoutSet> sets = workoutSetRepository.findForMetrics(
                EPOCH, LocalDate.now(), null, List.of(lift), SetFilter.WORKING_SETS.setTypes());
        return sets.stream()
                .filter(ws -> ws.getWeight() != null)
                .max(Comparator.comparingDouble(WorkoutSet::getWeight))
                .map(ws -> ws.getWorkoutEntry().getExercise().getId())
                .orElse(null);
    }

    private SbdSummary buildSbdSummary(Long athleteId, LocalDate today) {
        Double bodyweightKg = bodyweightProvider.bodyweightKg(athleteId, today);
        Sex sex = sexProvider.sex(athleteId);

        List<LiftSummary> lifts = new ArrayList<>();
        for (MainPattern lift : COMP_LIFTS) {
            ResolvedLift resolved = resolveLift(athleteId, lift);
            if (resolved.exerciseId() == null) {
                continue;
            }
            List<WorkoutSet> sets = workoutSetRepository.findForMetrics(
                    EPOCH, today, List.of(resolved.exerciseId()), null, SetFilter.WORKING_SETS.setTypes());
            if (sets.isEmpty()) {
                continue;
            }
            Exercise exercise = sets.get(0).getWorkoutEntry().getExercise();

            Double bestWeight = sets.stream().map(WorkoutSet::getWeight).filter(Objects::nonNull)
                    .max(Double::compareTo).orElse(null);
            Double bestE1rm = sets.stream()
                    .map(ws -> metricsCalculator.e1rm(ws, exercise, athleteId))
                    .filter(Objects::nonNull)
                    .max(Double::compareTo).orElse(null);
            Double xBodyweight = (bestWeight == null || bodyweightKg == null || bodyweightKg == 0)
                    ? null : round1(bestWeight / bodyweightKg);

            lifts.add(new LiftSummary(lift, resolved.exerciseId(), exercise.getBaseName(), resolved.configured(),
                    bestWeight == null ? null : round1(bestWeight),
                    bestE1rm == null ? null : round1(bestE1rm),
                    xBodyweight));
        }

        Double bestRealTotal = sumIfComplete(lifts, LiftSummary::bestWeight);
        Double bestE1rmTotal = sumIfComplete(lifts, LiftSummary::bestE1rm);

        Double dots = (bestRealTotal == null || bodyweightKg == null) ? null
                : round1(metricsCalculator.dots(bestRealTotal, bodyweightKg, sex));
        Double dotsE1rm = (bestE1rmTotal == null || bodyweightKg == null) ? null
                : round1(metricsCalculator.dots(bestE1rmTotal, bodyweightKg, sex));
        Double ipfGl = (bestRealTotal == null || bodyweightKg == null) ? null
                : round1(metricsCalculator.ipfGlPoints(bestRealTotal, bodyweightKg, sex));
        Double ipfGlE1rm = (bestE1rmTotal == null || bodyweightKg == null) ? null
                : round1(metricsCalculator.ipfGlPoints(bestE1rmTotal, bodyweightKg, sex));

        return new SbdSummary(bodyweightKg, bestRealTotal, bestE1rmTotal, dots, dotsE1rm, ipfGl, ipfGlE1rm, lifts);
    }

    private Double sumIfComplete(List<LiftSummary> lifts, Function<LiftSummary, Double> extractor) {
        if (lifts.size() < COMP_LIFTS.size()) {
            return null;
        }
        double total = 0;
        for (LiftSummary lift : lifts) {
            Double value = extractor.apply(lift);
            if (value == null) {
                return null;
            }
            total += value;
        }
        return round1(total);
    }

    private ConsistencySummary buildConsistencySummary(LocalDate from, LocalDate to, LocalDate today) {
        List<LocalDate> trainedDates = workoutSessionRepository.findDatesByStatusIn(List.of(Status.COMPLETED, Status.PARTIAL));
        var trainedWeekStarts = trainedDates.stream()
                .map(this::weekStart)
                .collect(Collectors.toCollection(TreeSet::new));

        int currentWeekStreak = computeCurrentStreak(trainedWeekStarts, today);
        int longestWeekStreak = computeLongestStreak(trainedWeekStarts);
        double avgSessionsPerWeek = computeAvgSessionsPerWeek(trainedDates, today);

        List<WorkoutSession> plannedSessions = workoutSessionRepository
                .findByDateBetweenAndTrainingSessionTemplateIsNotNull(from, to).stream()
                .filter(s -> !s.getDate().isAfter(today))
                .toList();

        int completed = (int) plannedSessions.stream().filter(s -> s.getStatus() == Status.COMPLETED).count();
        int partial = (int) plannedSessions.stream().filter(s -> s.getStatus() == Status.PARTIAL).count();
        int planned = plannedSessions.size();
        Double adherencePct = planned == 0 ? null : round1((completed + partial) * 100.0 / planned);

        return new ConsistencySummary(currentWeekStreak, longestWeekStreak, avgSessionsPerWeek,
                planned, completed, partial, adherencePct);
    }

    private LocalDate weekStart(LocalDate date) {
        return date.with(ChronoField.DAY_OF_WEEK, 1);
    }

    private int computeCurrentStreak(Set<LocalDate> trainedWeekStarts, LocalDate today) {
        LocalDate cursor = weekStart(today);
        if (!trainedWeekStarts.contains(cursor)) {
            cursor = cursor.minusWeeks(1);
        }
        int streak = 0;
        while (trainedWeekStarts.contains(cursor)) {
            streak++;
            cursor = cursor.minusWeeks(1);
        }
        return streak;
    }

    private int computeLongestStreak(Set<LocalDate> trainedWeekStarts) {
        int longest = 0;
        int current = 0;
        LocalDate previous = null;
        for (LocalDate week : trainedWeekStarts) {
            current = (previous != null && previous.plusWeeks(1).equals(week)) ? current + 1 : 1;
            longest = Math.max(longest, current);
            previous = week;
        }
        return longest;
    }

    private double computeAvgSessionsPerWeek(List<LocalDate> trainedDates, LocalDate today) {
        LocalDate currentWeekStart = weekStart(today);
        LocalDate windowStart = currentWeekStart.minusWeeks(CONSISTENCY_WINDOW_WEEKS);
        long count = trainedDates.stream()
                .filter(d -> !d.isBefore(windowStart) && d.isBefore(currentWeekStart))
                .count();
        return round1(count / (double) CONSISTENCY_WINDOW_WEEKS);
    }

    private TonnageSummary buildTonnageSummary(Long athleteId, LocalDate today) {
        // Tonelaje de por vida: cuenta todo el peso movido, calentamientos incluidos.
        // Mantiene el comportamiento previo (el filtro llegaba a null = sin filtrar).
        List<WorkoutSet> allSets = workoutSetRepository.findForMetrics(
                EPOCH, today, null, null, SetFilter.ALL_SETS.setTypes());

        double cumulative = 0;
        List<TonnageMilestoneCatalog.Entry> catalog = TonnageMilestoneCatalog.MILESTONES;
        Map<Double, LocalDate> achievedOn = new LinkedHashMap<>();
        int nextCatalogIndex = 0;

        for (WorkoutSet ws : allSets) {
            Exercise exercise = ws.getWorkoutEntry().getExercise();
            Double load = metricsCalculator.effectiveLoad(ws, exercise, athleteId);
            MeasurementType mt = exercise.getMeasurementType() == null ? MeasurementType.REPS : exercise.getMeasurementType();
            if (load != null && mt == MeasurementType.REPS && ws.getReps() != null) {
                cumulative += load * ws.getReps();
            }
            while (nextCatalogIndex < catalog.size() && cumulative >= catalog.get(nextCatalogIndex).thresholdKg()) {
                achievedOn.put(catalog.get(nextCatalogIndex).thresholdKg(), ws.getWorkoutEntry().getSession().getDate());
                nextCatalogIndex++;
            }
        }

        List<TonnageMilestone> milestones = catalog.stream()
                .map(m -> new TonnageMilestone(m.thresholdKg(), m.label(),
                        achievedOn.containsKey(m.thresholdKg()), achievedOn.get(m.thresholdKg())))
                .toList();

        TonnageMilestone next = milestones.stream().filter(m -> !m.achieved()).findFirst().orElse(null);
        Double pctToNext = next == null ? null : round1(cumulative / next.thresholdKg() * 100);

        return new TonnageSummary(round1(cumulative), milestones, next, pctToNext);
    }

    // ============================================================================
    // HELPERS
    // ============================================================================

    private Double average(List<Double> values) {
        if (values.isEmpty()) {
            return null;
        }
        return round1(values.stream().mapToDouble(Double::doubleValue).average().orElseThrow());
    }

    private double sum(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

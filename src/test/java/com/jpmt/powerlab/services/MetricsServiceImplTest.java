package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.metrics.*;
import com.jpmt.powerlab.repositories.TrainingSetTemplateRepository;
import com.jpmt.powerlab.repositories.WorkoutSessionRepository;
import com.jpmt.powerlab.repositories.WorkoutSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsServiceImplTest {

    @Mock
    private WorkoutSetRepository workoutSetRepository;
    @Mock
    private WorkoutSessionRepository workoutSessionRepository;
    @Mock
    private TrainingSetTemplateRepository setTemplateRepository;
    @Mock
    private MetricsCalculator metricsCalculator;
    @Mock
    private OneRepMaxProvider oneRepMaxProvider;
    @Mock
    private BodyweightProvider bodyweightProvider;
    @Mock
    private CompLiftProvider compLiftProvider;
    @Mock
    private SexProvider sexProvider;

    private MetricsServiceImpl metricsService;
    private long idCounter = 1;

    @BeforeEach
    void setUp() {
        metricsService = new MetricsServiceImpl(workoutSetRepository, workoutSessionRepository, setTemplateRepository,
                metricsCalculator, oneRepMaxProvider, bodyweightProvider, compLiftProvider, sexProvider);

        // Defaults sensatos para no tener que estubar todo en cada test; cada test sobreescribe lo que necesita.
        lenient().when(setTemplateRepository.findBySessionTemplateIdIn(any())).thenReturn(List.of());
        lenient().when(workoutSetRepository.findForMetrics(any(), any(), any(), any(), any())).thenReturn(List.of());
        lenient().when(workoutSessionRepository.findDatesByStatusIn(any())).thenReturn(List.of());
        lenient().when(workoutSessionRepository.findByDateBetweenAndTrainingSessionTemplateIsNotNull(any(), any()))
                .thenReturn(List.of());
        lenient().when(bodyweightProvider.bodyweightKg(any(), any())).thenReturn(88.5);
        lenient().when(sexProvider.sex(any())).thenReturn(Sex.MALE);
        lenient().when(compLiftProvider.compLiftExerciseId(any(), any())).thenReturn(Optional.empty());
        lenient().when(oneRepMaxProvider.referenceMax(any(), any())).thenReturn(Optional.empty());
        lenient().when(metricsCalculator.effectiveLoad(any(), any(), anyLong())).thenReturn(100.0);
        lenient().when(metricsCalculator.e1rm(any(), any(), anyLong())).thenReturn(null);
        lenient().when(metricsCalculator.isHardSet(any())).thenReturn(false);
        lenient().when(metricsCalculator.isFailedSet(any()))
                .thenAnswer(inv -> failed(inv.getArgument(0)));
        lenient().when(metricsCalculator.isValidLift(any()))
                .thenAnswer(inv -> {
                    WorkoutSet ws = inv.getArgument(0);
                    return ws.getWeight() != null && !failed(ws);
                });
        lenient().when(metricsCalculator.isEstimated(any(), any())).thenReturn(false);
    }

    // ============================================================================
    // Helpers de construcción de dominio
    // ============================================================================

    /** Mismo criterio que MetricsCalculatorImpl: RPE > 10 es intento fallado. */
    private static boolean failed(WorkoutSet set) {
        return set.getRpeValue() != null && set.getRpeValue() > 10.0;
    }

    private Exercise exercise(long id, String name, MainPattern pattern) {
        return Exercise.builder().id(id).baseName(name).mainPattern(pattern)
                .measurementType(MeasurementType.REPS).loadMode(LoadMode.EXTERNAL).build();
    }

    private WorkoutSession session(LocalDate date, TrainingSessionTemplate template) {
        return WorkoutSession.builder().id(idCounter++).date(date).trainingSessionTemplate(template)
                .status(Status.COMPLETED).build();
    }

    private WorkoutEntry entry(WorkoutSession session, Exercise exercise) {
        return WorkoutEntry.builder().id(idCounter++).session(session).exercise(exercise)
                .displayOrder(1).build();
    }

    private WorkoutSet set(WorkoutEntry entry, Double weight, Integer reps, Double rpe) {
        return WorkoutSet.builder().id(idCounter++).workoutEntry(entry).sequenceNumber(1)
                .weight(weight).reps(reps).rpeValue(rpe).build();
    }

    private WorkoutSession plannedSession(LocalDate date, Status status) {
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("Block A")
                .weekNumber(1).dayIndex(1).conjugatedDayType(ConjugatedDayType.NONE).build();
        return WorkoutSession.builder().id(idCounter++).date(date).trainingSessionTemplate(template)
                .status(status).build();
    }

    // ============================================================================
    // series() — agrupación y bucketing por periodo
    // ============================================================================

    @Test
    void series_week_groupsAcrossIsoYearBoundary() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession session1 = session(LocalDate.of(2020, 12, 28), null);
        WorkoutSession session2 = session(LocalDate.of(2021, 1, 3), null);
        WorkoutSet set1 = set(entry(session1, exercise), 100.0, 5, 8.0);
        WorkoutSet set2 = set(entry(session2, exercise), 105.0, 5, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), isNull(), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(set1, set2));

        MetricsQuery query = MetricsQuery.normalized(
                LocalDate.of(2020, 12, 1), LocalDate.of(2021, 1, 31),
                MetricsPeriod.WEEK, MetricsGroupBy.NONE, SetFilter.WORKING_SETS, null, null);

        MetricsResponse response = metricsService.series(query);

        assertEquals(1, response.series().size());
        MetricsSeries series = response.series().get(0);
        assertEquals(1, series.points().size());
        assertEquals("2020-W53", series.points().get(0).period());
        assertEquals(2, series.points().get(0).sets());
    }

    @Test
    void series_block_sessionWithoutTemplate_groupsAsSinBloque() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession freeSession = session(LocalDate.now(), null);
        WorkoutSet freeSet = set(entry(freeSession, exercise), 100.0, 5, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), isNull(), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(freeSet));

        MetricsQuery query = MetricsQuery.normalized(
                LocalDate.now().minusDays(7), LocalDate.now(),
                MetricsPeriod.BLOCK, MetricsGroupBy.NONE, SetFilter.WORKING_SETS, null, null);

        MetricsResponse response = metricsService.series(query);

        assertEquals("Sin bloque", response.series().get(0).points().get(0).period());
    }

    // ============================================================================
    // personalRecords()
    // ============================================================================

    @Test
    void personalRecords_heaviestSet_tieBreaksByRepsThenEarliestDate() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s1 = session(LocalDate.of(2024, 1, 10), null);
        WorkoutSession s2 = session(LocalDate.of(2024, 1, 5), null);
        WorkoutSession s3 = session(LocalDate.of(2024, 1, 1), null);
        WorkoutSet lowReps = set(entry(s1, exercise), 100.0, 5, 8.0);
        WorkoutSet highRepsLater = set(entry(s2, exercise), 100.0, 8, 8.0);
        WorkoutSet highRepsEarlier = set(entry(s3, exercise), 100.0, 8, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(lowReps, highRepsLater, highRepsEarlier));

        PersonalRecordsResponse response = metricsService.personalRecords(null, null, List.of(1L), null);

        ExercisePrs prs = response.exercises().get(0);
        assertEquals(100.0, prs.heaviestSet().weight());
        assertEquals(8, prs.heaviestSet().reps());
        assertEquals(LocalDate.of(2024, 1, 1), prs.heaviestSet().date());
    }

    @Test
    void personalRecords_repMaxes_bestWeightPerRepCount() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s1 = session(LocalDate.of(2024, 2, 1), null);
        WorkoutSession s2 = session(LocalDate.of(2024, 2, 2), null);
        WorkoutSet fiveRepSet = set(entry(s1, exercise), 120.0, 5, 8.0);
        WorkoutSet threeRepSet = set(entry(s2, exercise), 140.0, 3, 9.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(fiveRepSet, threeRepSet));

        PersonalRecordsResponse response = metricsService.personalRecords(null, null, List.of(1L), null);

        ExercisePrs prs = response.exercises().get(0);
        assertEquals(2, prs.repMaxes().size());
        assertEquals(140.0, prs.repMaxes().stream().filter(rm -> rm.reps() == 3).findFirst().orElseThrow().weight());
        assertEquals(120.0, prs.repMaxes().stream().filter(rm -> rm.reps() == 5).findFirst().orElseThrow().weight());
    }

    @Test
    void personalRecords_fallsBackToBestWeightWhenCompLiftNotConfigured() {
        Exercise lowBar = exercise(1L, "Low Bar Squat", MainPattern.SQUAT);
        Exercise highBar = exercise(2L, "High Bar Squat", MainPattern.SQUAT);
        WorkoutSession session = session(LocalDate.now().minusDays(10), null);
        WorkoutSet lowBarSet = set(entry(session, lowBar), 150.0, 3, 8.0);
        WorkoutSet highBarSet = set(entry(session, highBar), 120.0, 3, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), isNull(), eq(List.of(MainPattern.SQUAT)), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(lowBarSet, highBarSet));
        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(lowBarSet));

        PersonalRecordsResponse response = metricsService.personalRecords(null, null, null, null);

        assertEquals(1, response.exercises().size());
        assertEquals(1L, response.exercises().get(0).exerciseId());
    }

    @Test
    void personalRecords_failedAttempt_isNotARecord() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s1 = session(LocalDate.of(2024, 3, 1), null);
        WorkoutSession s2 = session(LocalDate.of(2024, 3, 8), null);
        WorkoutSet validSingle = set(entry(s1, exercise), 180.0, 1, 9.0);
        WorkoutSet failedSingle = set(entry(s2, exercise), 200.0, 1, 11.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(validSingle, failedSingle));

        ExercisePrs prs = metricsService.personalRecords(null, null, List.of(1L), null).exercises().get(0);

        assertEquals(180.0, prs.heaviestSet().weight());
        // El fallado de 200 no pisa el récord válido: repMaxes se queda en 180 y el fallo va a failedRepMaxes.
        assertEquals(180.0, prs.repMaxes().stream().filter(rm -> rm.reps() == 1).findFirst().orElseThrow().weight());
        assertEquals(200.0, prs.failedRepMaxes().stream().filter(rm -> rm.reps() == 1).findFirst().orElseThrow().weight());
        assertEquals(200.0, prs.bestFailedSingle().weight());
        assertEquals(LocalDate.of(2024, 3, 8), prs.bestFailedSingle().date());
    }

    @Test
    void personalRecords_failedRepMaxes_keepsHeaviestFailedPerRepCount() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s1 = session(LocalDate.of(2024, 4, 1), null);
        WorkoutSession s2 = session(LocalDate.of(2024, 4, 8), null);
        WorkoutSession s3 = session(LocalDate.of(2024, 4, 15), null);
        WorkoutSet failedLighter = set(entry(s1, exercise), 120.0, 3, 11.0);
        WorkoutSet failedHeavier = set(entry(s2, exercise), 130.0, 3, 11.0);
        WorkoutSet validThree = set(entry(s3, exercise), 110.0, 3, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(failedLighter, failedHeavier, validThree));

        ExercisePrs prs = metricsService.personalRecords(null, null, List.of(1L), null).exercises().get(0);

        assertEquals(130.0, prs.failedRepMaxes().stream().filter(rm -> rm.reps() == 3).findFirst().orElseThrow().weight());
        assertEquals(110.0, prs.repMaxes().stream().filter(rm -> rm.reps() == 3).findFirst().orElseThrow().weight());
    }

    @Test
    void personalRecords_referenceMax_comesFromProvider() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s = session(LocalDate.of(2024, 5, 1), null);
        WorkoutSet validSet = set(entry(s, exercise), 150.0, 3, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(validSet));
        when(oneRepMaxProvider.referenceMax(any(), eq(1L))).thenReturn(Optional.of(185.0));

        ExercisePrs prs = metricsService.personalRecords(null, null, List.of(1L), null).exercises().get(0);

        assertEquals(185.0, prs.referenceMax());
    }

    @Test
    void personalRecords_referenceMax_nullWhenNotConfigured() {
        Exercise exercise = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s = session(LocalDate.of(2024, 5, 1), null);
        WorkoutSet validSet = set(entry(s, exercise), 150.0, 3, 8.0);

        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(), eq(SetFilter.WORKING_SETS.setTypes())))
                .thenReturn(List.of(validSet));

        ExercisePrs prs = metricsService.personalRecords(null, null, List.of(1L), null).exercises().get(0);

        assertNull(prs.referenceMax());
    }

    @Test
    void personalRecords_fromAfterTo_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> metricsService.personalRecords(LocalDate.now(), LocalDate.now().minusDays(1), null, null));
    }

    @Test
    void personalRecords_exerciseIdsAndPatterns_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> metricsService.personalRecords(null, null, List.of(1L), List.of(MainPattern.SQUAT)));
    }

    // ============================================================================
    // summary() — consistencia (rachas, adherencia con PARTIAL)
    // ============================================================================

    @Test
    void summary_consistency_streaksAndAdherenceWithPartialSessions() {
        LocalDate today = LocalDate.now();
        LocalDate thisWeekStart = today.with(ChronoField.DAY_OF_WEEK, 1);

        LocalDate trainedLastWeek = thisWeekStart.minusWeeks(1).plusDays(2);
        LocalDate trainedFiveWeeksAgo = thisWeekStart.minusWeeks(5).plusDays(2);
        LocalDate trainedSixWeeksAgo = thisWeekStart.minusWeeks(6).plusDays(2);

        when(workoutSessionRepository.findDatesByStatusIn(any()))
                .thenReturn(List.of(trainedSixWeeksAgo, trainedFiveWeeksAgo, trainedLastWeek));

        List<WorkoutSession> planned = List.of(
                plannedSession(today.minusDays(5), Status.COMPLETED),
                plannedSession(today.minusDays(10), Status.COMPLETED),
                plannedSession(today.minusDays(15), Status.PARTIAL),
                plannedSession(today.minusDays(20), Status.SKIPPED));
        when(workoutSessionRepository.findByDateBetweenAndTrainingSessionTemplateIsNotNull(any(), any()))
                .thenReturn(planned);

        MetricsSummaryResponse response = metricsService.summary(null, null);

        assertEquals(1, response.consistency().currentWeekStreak());
        assertEquals(2, response.consistency().longestWeekStreak());
        // round1 usa Math.round (mitad hacia arriba): 1/4 = 0.25 -> 2.5 -> 0.3
        assertEquals(0.3, response.consistency().avgSessionsPerWeek());
        assertEquals(4, response.consistency().plannedSessions());
        assertEquals(2, response.consistency().completedSessions());
        assertEquals(1, response.consistency().partialSessions());
        assertEquals(75.0, response.consistency().adherencePct());
    }

    /** Resuelve el movimiento por patrón (sin comp lift configurado) y luego por ejercicio. */
    private void stubLift(MainPattern pattern, long exerciseId, List<WorkoutSet> sets) {
        when(workoutSetRepository.findForMetrics(any(), any(), isNull(), eq(List.of(pattern)),
                eq(SetFilter.WORKING_SETS.setTypes()))).thenReturn(sets);
        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(exerciseId)), isNull(),
                eq(SetFilter.WORKING_SETS.setTypes()))).thenReturn(sets);
    }

    private List<WorkoutSet> liftSets(long exerciseId, String name, MainPattern pattern,
                                       double singleWeight, double multiRepWeight) {
        Exercise exercise = exercise(exerciseId, name, pattern);
        WorkoutSession s = session(LocalDate.now().minusDays(7), null);
        return List.of(set(entry(s, exercise), singleWeight, 1, 9.0),
                set(entry(s, exercise), multiRepWeight, 5, 8.0));
    }

    @Test
    void summary_sbd_realTotalSumsValidSinglesNotHeaviestSets() {
        // El 5RM es más pesado que el single en los tres movimientos: no debe entrar en el total.
        stubLift(MainPattern.SQUAT, 1L, liftSets(1L, "Sentadilla", MainPattern.SQUAT, 200.0, 210.0));
        stubLift(MainPattern.BENCH, 2L, liftSets(2L, "Press banca", MainPattern.BENCH, 120.0, 130.0));
        stubLift(MainPattern.DEADLIFT, 3L, liftSets(3L, "Peso muerto", MainPattern.DEADLIFT, 240.0, 250.0));
        // Si DOTS se calculara sobre otro total, el stub quedaría sin usar y Mockito lo delataría.
        when(metricsCalculator.dots(560.0, 88.5, Sex.MALE)).thenReturn(391.3);

        SbdSummary sbd = metricsService.summary(null, null).sbd();

        assertEquals(560.0, sbd.bestRealTotal());
        LiftSummary squat = sbd.lifts().stream().filter(l -> l.lift() == MainPattern.SQUAT).findFirst().orElseThrow();
        assertEquals(200.0, squat.bestSingle().weight());
        assertEquals(210.0, squat.bestWeight());
        assertNull(squat.bestFailedSingle());
        assertEquals(391.3, sbd.dots());
    }

    @Test
    void summary_sbd_failedSingleExcludedFromTotalAndBestWeight() {
        Exercise squat = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s = session(LocalDate.now().minusDays(7), null);
        List<WorkoutSet> squatSets = List.of(
                set(entry(s, squat), 200.0, 1, 9.0),
                set(entry(s, squat), 220.0, 1, 11.0));
        stubLift(MainPattern.SQUAT, 1L, squatSets);
        stubLift(MainPattern.BENCH, 2L, liftSets(2L, "Press banca", MainPattern.BENCH, 120.0, 110.0));
        stubLift(MainPattern.DEADLIFT, 3L, liftSets(3L, "Peso muerto", MainPattern.DEADLIFT, 240.0, 230.0));

        SbdSummary sbd = metricsService.summary(null, null).sbd();

        LiftSummary squatSummary = sbd.lifts().stream()
                .filter(l -> l.lift() == MainPattern.SQUAT).findFirst().orElseThrow();
        assertEquals(200.0, squatSummary.bestSingle().weight());
        assertEquals(200.0, squatSummary.bestWeight());
        assertEquals(220.0, squatSummary.bestFailedSingle().weight());
        assertEquals(560.0, sbd.bestRealTotal());
    }

    @Test
    void summary_sbd_missingSingleInOneLift_leavesTotalAndScoresNull() {
        Exercise deadlift = exercise(3L, "Peso muerto", MainPattern.DEADLIFT);
        WorkoutSession s = session(LocalDate.now().minusDays(7), null);
        stubLift(MainPattern.SQUAT, 1L, liftSets(1L, "Sentadilla", MainPattern.SQUAT, 200.0, 190.0));
        stubLift(MainPattern.BENCH, 2L, liftSets(2L, "Press banca", MainPattern.BENCH, 120.0, 110.0));
        stubLift(MainPattern.DEADLIFT, 3L, List.of(set(entry(s, deadlift), 240.0, 3, 8.0)));

        SbdSummary sbd = metricsService.summary(null, null).sbd();

        assertNull(sbd.bestRealTotal());
        assertNull(sbd.dots());
        assertNull(sbd.ipfGl());
    }

    @Test
    void summary_sbd_referenceMax_fromProviderWhenCompLiftConfigured() {
        Exercise squat = exercise(1L, "Sentadilla", MainPattern.SQUAT);
        WorkoutSession s = session(LocalDate.now().minusDays(7), null);
        List<WorkoutSet> squatSets = List.of(set(entry(s, squat), 200.0, 1, 9.0));

        when(compLiftProvider.compLiftExerciseId(any(), eq(MainPattern.SQUAT))).thenReturn(Optional.of(1L));
        when(workoutSetRepository.findForMetrics(any(), any(), eq(List.of(1L)), isNull(),
                eq(SetFilter.WORKING_SETS.setTypes()))).thenReturn(squatSets);
        when(oneRepMaxProvider.referenceMax(any(), eq(1L))).thenReturn(Optional.of(210.0));

        SbdSummary sbd = metricsService.summary(null, null).sbd();

        LiftSummary squatSummary = sbd.lifts().stream()
                .filter(l -> l.lift() == MainPattern.SQUAT).findFirst().orElseThrow();
        assertTrue(squatSummary.compLiftConfigured());
        assertEquals(210.0, squatSummary.referenceMax());
    }

    @Test
    void summary_fromAfterTo_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> metricsService.summary(LocalDate.now(), LocalDate.now().minusDays(1)));
    }
}

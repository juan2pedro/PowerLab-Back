package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.LastExerciseResponse;
import com.jpmt.powerlab.models.dto.workoutentry.WorkoutEntryCreateRequest;
import com.jpmt.powerlab.models.dto.workoutentry.WorkoutEntryResponse;
import com.jpmt.powerlab.models.dto.workoutentry.WorkoutEntryUpdateRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.powerlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.powerlab.models.dto.workoutset.WorkoutSetResponse;
import com.jpmt.powerlab.models.dto.workoutset.WorkoutSetWithExerciseRequest;
import com.jpmt.powerlab.models.mappers.WorkoutEntryMapper;
import com.jpmt.powerlab.models.mappers.WorkoutSessionMapper;
import com.jpmt.powerlab.models.mappers.WorkoutSetMapper;
import com.jpmt.powerlab.repositories.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutEntryRepository entryRepository;
    private final TrainingSessionTemplateRepository templateRepository;
    private final TrainingSetTemplateRepository setTemplateRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionMapper workoutSessionMapper;
    private final WorkoutEntryMapper entryMapper;
    private final WorkoutSetMapper workoutSetMapper;
    private final PrescribedWeightResolver prescribedWeightResolver;

    public WorkoutServiceImpl(WorkoutSessionRepository sessionRepository,
                              WorkoutEntryRepository entryRepository,
                              TrainingSessionTemplateRepository templateRepository,
                              TrainingSetTemplateRepository setTemplateRepository,
                              ExerciseRepository exerciseRepository, WorkoutSetRepository workoutSetRepository,
                              WorkoutSessionMapper workoutSessionMapper,
                              WorkoutEntryMapper entryMapper, WorkoutSetMapper workoutSetMapper,
                              PrescribedWeightResolver prescribedWeightResolver) {
        this.sessionRepository = sessionRepository;
        this.entryRepository = entryRepository;
        this.templateRepository = templateRepository;
        this.setTemplateRepository = setTemplateRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutSessionMapper = workoutSessionMapper;
        this.entryMapper = entryMapper;
        this.workoutSetMapper = workoutSetMapper;
        this.prescribedWeightResolver = prescribedWeightResolver;
    }

    @Transactional
    @Override
    public WorkoutSessionDetailResponse saveWorkoutSession(WorkoutSessionRequest sessionRequest) {
        assertNoWorkoutOnDate(sessionRequest.date());
        WorkoutSession session = workoutSessionMapper.toEntity(sessionRequest);
        applyDefaultStatus(session);

        if (sessionRequest.trainingSessionTemplateId() != null) {
            TrainingSessionTemplate template = templateRepository.findById(sessionRequest.trainingSessionTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", sessionRequest.trainingSessionTemplateId()));
            session.setTrainingSessionTemplate(template);
        }

        WorkoutSession saved = sessionRepository.save(session);
        return workoutSessionMapper.toDetailResponse(saved);
    }

    @Transactional
    @Override
    public WorkoutSessionDetailResponse createWorkoutFromTemplate(Long templateId, WorkoutSessionRequest sessionRequest) {
        assertNoWorkoutOnDate(sessionRequest.date());
        TrainingSessionTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", templateId));

        WorkoutSession session = workoutSessionMapper.toEntity(sessionRequest);
        applyDefaultStatus(session);
        session.setTrainingSessionTemplate(template);

        WorkoutSession saved = sessionRepository.save(session);
        return workoutSessionMapper.toDetailResponse(saved);
    }

    /** Una sesión recién creada está en curso salvo que el cliente indique otro estado. */
    private void applyDefaultStatus(WorkoutSession session) {
        if (session.getStatus() == null) {
            session.setStatus(Status.IN_PROGRESS);
        }
    }

    @Transactional
    @Override
    public WorkoutSessionDetailResponse updateWorkoutStatus(Long id, Status status) {
        WorkoutSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", id));
        session.setStatus(status);
        return workoutSessionMapper.toDetailResponse(sessionRepository.save(session));
    }

    /**
     * Solo puede existir un workout por fecha (el modelo de la app trabaja con "el día").
     * Permitir duplicados rompía el GET /day, que devuelve un único workout.
     */
    private void assertNoWorkoutOnDate(LocalDate date) {
        if (date != null && sessionRepository.existsByDate(date)) {
            throw new BadRequestException("Ya existe un entreno para el " + date);
        }
    }

    @Override
    public List<WorkoutSessionSummaryResponse> findAllWorkoutSessionsByDateRange(LocalDate from, LocalDate to) {
      List  <WorkoutSession> session = sessionRepository.findFullByDateRange(from, to);
        return session.stream()
                .map(workoutSessionMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional <WorkoutDayResponse> findFullDayByDate(LocalDate date) {

        Optional<WorkoutSession> found = sessionRepository.findFullByDate(date).stream().findFirst();

        if (found.isEmpty()) {
            return Optional.empty();
        }
        WorkoutSession session = found.get();
        Long templateId = session.getTrainingSessionTemplate() != null ? session.getTrainingSessionTemplate().getId() : null;

        // If the workout references a template but the template can't be loaded, return empty (not found/204)
        if (templateId != null && templateRepository.findByIdOrderByDisplayOrder(templateId).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(loadFullDay(session, templateId));

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkoutDayResponse> findFullDayById(Long id) {
        Optional<WorkoutSession> found = sessionRepository.findHeaderById(id);
        if (found.isEmpty()){
            return Optional.empty();
        }
        WorkoutSession session = found.get();
        Long templateId = session.getTrainingSessionTemplate() != null ? session.getTrainingSessionTemplate().getId() : null;

        if (templateId != null && templateRepository.findByIdOrderByDisplayOrder(templateId).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(loadFullDay(session, templateId));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionDetailResponse findWorkoutById(Long id) {
        WorkoutSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", id));
        return workoutSessionMapper.toDetailResponse(session);
    }

    @Transactional
    @Override
    public void deleteWorkoutById(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("WorkoutSession", "id", id);
        }
        sessionRepository.deleteById(id);
    }

    @Transactional
    @Override
    public WorkoutEntryResponse saveWorkoutEntry(WorkoutEntryCreateRequest entryRequest) {
        WorkoutSession session = sessionRepository.findById(entryRequest.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", entryRequest.sessionId()));

        Exercise exercise = exerciseRepository.findById(entryRequest.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", entryRequest.exerciseId()));

        WorkoutEntry workoutEntry = entryMapper.toEntity(entryRequest);
        workoutEntry.setSession(session);
        workoutEntry.setExercise(exercise);

        if (entryRequest.sourceTemplateId() != null) {
            TrainingSetTemplate sourceTemplate = setTemplateRepository.findById(entryRequest.sourceTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingSetTemplate", "id", entryRequest.sourceTemplateId()));
            workoutEntry.setSourceTemplate(sourceTemplate);
        }

        WorkoutEntry saved = entryRepository.save(workoutEntry);
        return entryMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public WorkoutEntryResponse updateEntry(Long id, WorkoutEntryUpdateRequest entryRequest) {
        WorkoutEntry workoutEntry = entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutEntry", "id", id));

        entryMapper.updateEntityFromRequest(entryRequest, workoutEntry);
        WorkoutEntry saved = entryRepository.save(workoutEntry);
        return entryMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteWorkoutEntryById(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new ResourceNotFoundException("WorkoutEntry", "id", id);
        }
        entryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<WorkoutSetResponse> findAllWorkoutSets(Long entryId) {
        List<WorkoutSet> sets = workoutSetRepository.findByWorkoutEntryId(entryId);
        return sets.stream().map(workoutSetMapper::toResponse).toList();
    }

    @Transactional
    @Override
    public WorkoutSetResponse saveWorkoutSet(Long entryId, @Valid WorkoutSetRequest workoutSetRequest) {
        WorkoutEntry workoutEntry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutEntry", "id", entryId));

        WorkoutSet set = workoutSetMapper.toEntity(workoutSetRequest);
        set.setWorkoutEntry(workoutEntry); // Asignar la entrada al set

        WorkoutSet saved = workoutSetRepository.save(set);
        return workoutSetMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public WorkoutDayResponse.WorkoutDayEntry addSetByExercise(Long workoutId, @Valid WorkoutSetWithExerciseRequest request) {
        WorkoutSession session = sessionRepository.findHeaderById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", workoutId));

        if (session.getTrainingSessionTemplate() == null) {
            throw new BadRequestException("workout has no template, cannot validate exerciseId");
        }

        Long templateId = session.getTrainingSessionTemplate().getId();
        TrainingSessionTemplate fullTemplate = templateRepository.findByIdOrderByDisplayOrder(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", templateId));

        TrainingSetTemplate matchingSetTemplate;
        if (request.sourceTemplateId() != null) {
            matchingSetTemplate = fullTemplate.getTrainingSets().stream()
                    .filter(st -> request.sourceTemplateId().equals(st.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "sourceTemplateId " + request.sourceTemplateId() + " no pertenece a este workout"));
        } else {
            matchingSetTemplate = fullTemplate.getTrainingSets().stream()
                    .filter(st -> st.getExercise() != null && request.exerciseId().equals(st.getExercise().getId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "exercise " + request.exerciseId() + " no planificado en este workout"));
        }

        final Long resolvedSourceTemplateId = matchingSetTemplate.getId();
        WorkoutEntry entry = session.getEntries().stream()
                .filter(e -> e.getExercise() != null && request.exerciseId().equals(e.getExercise().getId())
                        && Objects.equals(
                                e.getSourceTemplate() != null ? e.getSourceTemplate().getId() : null,
                                resolvedSourceTemplateId))
                .findFirst()
                .orElseGet(() -> {
                    WorkoutEntry newEntry = WorkoutEntry.builder()
                            .session(session)
                            .exercise(matchingSetTemplate.getExercise())
                            .notes(matchingSetTemplate.getNotes())
                            .displayOrder(matchingSetTemplate.getDisplayOrder())
                            .sourceTemplate(matchingSetTemplate)
                            .build();
                    WorkoutEntry saved = entryRepository.save(newEntry);
                    session.getEntries().add(saved);
                    return saved;
                });

        WorkoutSet set = WorkoutSet.builder()
                .workoutEntry(entry)
                .sequenceNumber(request.sequenceNumber())
                .reps(request.reps())
                .weight(request.weight())
                .rpeValue(request.rpeValue())
                .type(request.type() != null ? request.type() : SetType.WORKING)
                .build();

        WorkoutSet savedSet = workoutSetRepository.save(set);
        entry.getSets().add(savedSet);

        return workoutSessionMapper.toEntry(entry);
    }

    @Transactional
    @Override
    public WorkoutSetResponse updateWorkoutSet(Long id, @Valid WorkoutSetRequest workoutSet) {
        WorkoutSet existingSet = workoutSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSet", "id", id));

        workoutSetMapper.updateEntityFromRequest(workoutSet, existingSet);
        WorkoutSet saved = workoutSetRepository.save(existingSet);
        return workoutSetMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteWorkoutSetById(Long id) {
        if (!workoutSetRepository.existsById(id)) {
            throw new ResourceNotFoundException("WorkoutSet", "id", id);
        }
        workoutSetRepository.deleteById(id);
    }



    @Transactional(readOnly = true)
    public WorkoutDayResponse loadFullDay(WorkoutSession workoutSession, Long templateId) {
        Map<Long, PrescribedWeightResolver.ResolvedPrescription> resolvedWeights = Map.of();
        if (templateId != null) {
            TrainingSessionTemplate fullTemplate = templateRepository.findByIdOrderByDisplayOrder(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", templateId));
            workoutSession.setTrainingSessionTemplate(fullTemplate);
            resolvedWeights = prescribedWeightResolver.resolveAll(fullTemplate.getTrainingSets(), 1L);
        }

        if( workoutSession.getEntries().isEmpty()){
            return workoutSessionMapper.toDayResponse(workoutSession, resolvedWeights);
        }
        List<Long> entryIds = workoutSession.getEntries().stream().map(WorkoutEntry::getId).toList();
        List<WorkoutSet> allSets = workoutSetRepository.findByWorkoutEntryIdIn(entryIds);

        Map<Long, List<WorkoutSet>> setsByEntryId = allSets.stream()
                .collect(Collectors.groupingBy(s -> s.getWorkoutEntry().getId()));

        for (WorkoutEntry entry : workoutSession.getEntries()) {
            LinkedHashSet<WorkoutSet> orderedSets = setsByEntryId
                    .getOrDefault(entry.getId(), List.of()).stream()
                    .sorted(Comparator.comparingInt(s -> s.getSequenceNumber() == null ? 0 : s.getSequenceNumber()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            entry.setSets(orderedSets);
        }
        return workoutSessionMapper.toDayResponse(workoutSession, resolvedWeights);
    }

    @Override
    public LastExerciseResponse findLastSetByExerciseId(Long exerciseId) {
        WorkoutSet lastSet = workoutSetRepository.findLatestForExercise(exerciseId, PageRequest.of(0, 1))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSet", "exerciseId", exerciseId));

        return workoutSetMapper.toLastExerciseResponse(lastSet);
    }


}

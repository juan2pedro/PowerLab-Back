package com.jpmt.strengthlab.services;

import com.jpmt.strengthlab.exceptions.ResourceNotFoundException;
import com.jpmt.strengthlab.models.domain.*;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryCreateRequest;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryResponse;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryUpdateRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetResponse;
import com.jpmt.strengthlab.models.mappers.WorkoutEntryMapper;
import com.jpmt.strengthlab.models.mappers.WorkoutSessionMapper;
import com.jpmt.strengthlab.models.mappers.WorkoutSetMapper;
import com.jpmt.strengthlab.repositories.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutEntryRepository entryRepository;
    private final TrainingSessionTemplateRepository templateRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionMapper workoutSessionMapper;
    private final WorkoutEntryMapper entryMapper;
    private final WorkoutSetMapper workoutSetMapper;

    public WorkoutServiceImpl(WorkoutSessionRepository sessionRepository,
                              WorkoutEntryRepository entryRepository,
                              TrainingSessionTemplateRepository templateRepository,
                              ExerciseRepository exerciseRepository, WorkoutSetRepository workoutSetRepository,
                              WorkoutSessionMapper workoutSessionMapper,
                              WorkoutEntryMapper entryMapper, WorkoutSetMapper workoutSetMapper) {
        this.sessionRepository = sessionRepository;
        this.entryRepository = entryRepository;
        this.templateRepository = templateRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutSessionMapper = workoutSessionMapper;
        this.entryMapper = entryMapper;
        this.workoutSetMapper = workoutSetMapper;
    }

    @Transactional
    @Override
    public WorkoutSessionDetailResponse saveWorkoutSession(WorkoutSessionRequest sessionRequest) {
        WorkoutSession session = workoutSessionMapper.toEntity(sessionRequest);

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
        TrainingSessionTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", templateId));

        WorkoutSession session = workoutSessionMapper.toEntity(sessionRequest);
        session.setTrainingSessionTemplate(template);

        WorkoutSession saved = sessionRepository.save(session);
        return workoutSessionMapper.toDetailResponse(saved);
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
    public WorkoutDayResponse findFullDayByDate(LocalDate date) {
          WorkoutSession session = sessionRepository.findFullByDate(date).orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "date", date));
        Long templateId = session.getTrainingSessionTemplate() != null ? session.getTrainingSessionTemplate().getId() : null;

        return this.loadFullDay(session,templateId);

    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutDayResponse findFullDayById(Long id) {
        WorkoutSession session = sessionRepository.findHeaderById(id).orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", id));
        Long templateId = session.getTrainingSessionTemplate() != null ? session.getTrainingSessionTemplate().getId() : null;

        return loadFullDay(session,templateId);
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
        if (templateId != null) {
            TrainingSessionTemplate fullTemplate = templateRepository.findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingSessionTemplate", "id", templateId));
            workoutSession.setTrainingSessionTemplate(fullTemplate);
        }

        if( workoutSession.getEntries().isEmpty()){
            return workoutSessionMapper.toDayResponse(workoutSession);
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
        return workoutSessionMapper.toDayResponse(workoutSession);
    }
}

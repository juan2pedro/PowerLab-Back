package com.jpmt.strengthlab.services;

import com.jpmt.strengthlab.exceptions.ResourceNotFoundException;
import com.jpmt.strengthlab.models.domain.Exercise;
import com.jpmt.strengthlab.models.domain.TrainingSessionTemplate;
import com.jpmt.strengthlab.models.domain.TrainingSetTemplate;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.strengthlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateRequest;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateUpdateRequest;
import com.jpmt.strengthlab.models.mappers.TrainingSessionTemplateMapper;
import com.jpmt.strengthlab.models.mappers.TrainingSetTemplateMapper;
import com.jpmt.strengthlab.repositories.ExerciseRepository;
import com.jpmt.strengthlab.repositories.TrainingSessionTemplateRepository;
import com.jpmt.strengthlab.repositories.TrainingSetTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private static final String TSESSIONT = "TrainingSessionTemplate";

    private final TrainingSessionTemplateRepository sessionRepository;
    private final TrainingSetTemplateRepository setRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingSessionTemplateMapper sessionMapper;
    private final TrainingSetTemplateMapper setMapper;

    public TrainingSessionServiceImpl(TrainingSessionTemplateRepository sessionRepository, TrainingSetTemplateRepository setRepository, ExerciseRepository exerciseRepository, TrainingSessionTemplateMapper sessionMapper, TrainingSetTemplateMapper setMapper) {
        this.sessionRepository = sessionRepository;
        this.setRepository = setRepository;
        this.exerciseRepository = exerciseRepository;
        this.sessionMapper = sessionMapper;
        this.setMapper = setMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingSessionTemplateSummaryResponse> findAllSession() {
        List<TrainingSessionTemplate> entities = sessionRepository.findAll();
        return entities.stream().map(sessionMapper::toSummaryResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingSessionTemplateSummaryResponse> findAllSessionByBlockNameAndWeekNumber(String blockName, Integer weekNumber) {
        List<TrainingSessionTemplate> entities = sessionRepository.findByBlockNameAndWeekNumberOrderByDayInWeekAsc(blockName, weekNumber);
        return entities.stream().map(sessionMapper::toSummaryResponse).toList();
    }

    @Transactional
    @Override
    public TrainingSessionTemplateDetailResponse saveSession(TrainingSessionTemplateRequest request) {
        TrainingSessionTemplate entity = sessionMapper.toEntity(request);
        TrainingSessionTemplate saved = sessionRepository.save(entity);
        return sessionMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional
    public TrainingSessionTemplateDetailResponse updateSession(Long id, TrainingSessionTemplateRequest request) {
        TrainingSessionTemplate entity = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TSESSIONT, "id", id));

        sessionMapper.updateEntityFromRequest(request, entity);
        TrainingSessionTemplate saved = sessionRepository.save(entity);
        return sessionMapper.toDetailResponse(saved);
    }

    @Transactional
    @Override
    public void deleteSessionById(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new ResourceNotFoundException(TSESSIONT, "id", id);
        }
        sessionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingSessionTemplateDetailResponse findSessionById(Long id) {
        TrainingSessionTemplate entity = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TSESSIONT, "id", id));
        return sessionMapper.toDetailResponse(entity);
    }

    @Transactional
    @Override
    public TrainingSetTemplateResponse saveSet(Long sessionId, TrainingSetTemplateRequest request) {
        TrainingSessionTemplate session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(TSESSIONT, "id", sessionId));

        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", request.exerciseId()));

        TrainingSetTemplate setTemplate = setMapper.toEntity(request);
        setTemplate.setSessionTemplate(session);
        setTemplate.setExercise(exercise);

        TrainingSetTemplate saved = setRepository.save(setTemplate);
        return setMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TrainingSetTemplateResponse updateSet(Long id, TrainingSetTemplateUpdateRequest request) {
        TrainingSetTemplate entity = setRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSetTemplate", "id", id));

        setMapper.updateEntityFromRequest(request, entity);

        if (request.exerciseId() != null) {
            Exercise exercise = exerciseRepository.findById(request.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", request.exerciseId()));
            entity.setExercise(exercise);
        }

        TrainingSetTemplate saved = setRepository.save(entity);
        return setMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteSetById(Long id) {
        if (!setRepository.existsById(id)) {
            throw new ResourceNotFoundException("TrainingSetTemplate", "id", id);
        }
        setRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingSetTemplateResponse findSetById(Long id) {
        TrainingSetTemplate entity = setRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSetTemplate", "id", id));
        return setMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingSetTemplateResponse> findBySessionIdWithExercise(Long sessionId) {
        TrainingSessionTemplate session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(TSESSIONT, "id", sessionId));

        return session.getSetTemplates().stream()
                .map(setMapper::toResponse)
                .toList();
    }
}

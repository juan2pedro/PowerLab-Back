package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.Exercise;
import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.dto.exercise.ExerciseRequest;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.mappers.ExerciseMapper;
import com.jpmt.powerlab.repositories.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository repository;
    private final ExerciseMapper exerciseMapper;

    public ExerciseServiceImpl(ExerciseRepository repository, ExerciseMapper exerciseMapper) {
        this.repository = repository;
        this.exerciseMapper = exerciseMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExerciseResponse> findAll() {
        List<Exercise> entities = repository.findAll();
        return entities.stream().map(exerciseMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExerciseResponse> findAllByMainPattern(MainPattern mainPattern) {
        List<Exercise> entities = repository.findByMainPattern(mainPattern);
        return entities.stream().map(exerciseMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ExerciseResponse findById(Long id) {
        Exercise exercise = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        return exerciseMapper.toResponse(exercise);
    }

    @Transactional
    @Override
    public ExerciseResponse save(ExerciseRequest request) {
        Exercise entity = exerciseMapper.toEntity(request);
        Exercise saved = repository.save(entity);
        return exerciseMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public ExerciseResponse update(Long id, ExerciseRequest request) {
        Exercise entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        exerciseMapper.updateEntityFromRequest(request, entity);
        Exercise saved = repository.save(entity);
        return exerciseMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Exercise", "id", id);
        }
        repository.deleteById(id);
    }
}

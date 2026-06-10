package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.dto.exercise.ExerciseRequest;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;

import java.util.List;

public interface ExerciseService {
    List<ExerciseResponse> findAll();

    List<ExerciseResponse> findAllByMainPattern(MainPattern mainPattern);

    ExerciseResponse findById(Long id);

    ExerciseResponse save(ExerciseRequest exercise);

    ExerciseResponse update(Long id, ExerciseRequest exercise);

    void deleteById(Long id);
}

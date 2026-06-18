package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.IntensityType;
import com.jpmt.powerlab.models.dto.exercise.ExerciseInfo;

public record TrainingSetTemplateResponse(
        Long id,
        Integer targetSets,
        Integer targetReps,
        Double targetWeight,
        Integer targetIntensity,
        IntensityType targetIntensityType,
        String rest,
        String notes,
        Integer displayOrder,
        ExerciseInfo exercise
) {
}

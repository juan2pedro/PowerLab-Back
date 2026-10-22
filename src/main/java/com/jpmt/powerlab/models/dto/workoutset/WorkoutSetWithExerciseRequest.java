package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.SetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutSetWithExerciseRequest(
        @NotNull(message = "exerciseId is required")
        Long exerciseId,
        Long sourceTemplateId,
        @NotNull
        @Min(1)
        Integer sequenceNumber,
        @NotNull
        @Min(1)
        Integer reps,
        @Min(0)
        Double weight,
        @Min(0)
        @Max(11)
        Double rpeValue,
        SetType type
) {
}

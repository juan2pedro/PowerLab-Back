package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.IntensityType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutSetWithExerciseRequest(
        @NotNull(message = "exerciseId is required")
        Long exerciseId,
        @NotNull
        @Min(1)
        Integer sequenceNumber,
        @NotNull
        @Min(1)
        Integer reps,
        @NotNull
        @Min(0)
        Double weight,
        @NotNull
        IntensityType intensityType,
        @NotNull
        @Min(0)
        @Max(10)
        Integer intensityValue
) {
}

package com.jpmt.strengthlab.models.dto.workoutset;

import com.jpmt.strengthlab.models.domain.IntensityType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutSetRequest(
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
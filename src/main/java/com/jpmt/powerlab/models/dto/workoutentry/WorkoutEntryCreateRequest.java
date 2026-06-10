package com.jpmt.powerlab.models.dto.workoutentry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WorkoutEntryCreateRequest(
        @NotNull(message = "sessionId is required")
        Long sessionId,
        @NotNull(message = "exerciseId is required")
        Long exerciseId,
        @NotNull(message = "actualSets is required")
        @Min(value = 1, message = "actualSets must be at least 1")
        Integer actualSets,
        @NotNull(message = "actualReps is required")
        @Min(value = 1, message = "actualReps must be at least 1")
        Integer actualReps,
        Double actualPlateWeight,
        String actualRirOrRpe,
        Boolean isWarmup,
        String notes
) {
}

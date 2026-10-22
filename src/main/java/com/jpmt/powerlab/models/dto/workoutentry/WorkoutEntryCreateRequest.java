package com.jpmt.powerlab.models.dto.workoutentry;

import jakarta.validation.constraints.NotNull;

public record WorkoutEntryCreateRequest(
        @NotNull(message = "sessionId is required")
        Long sessionId,
        @NotNull(message = "exerciseId is required")
        Long exerciseId,
        Integer displayOrder,
        String notes,
        Long sourceTemplateId
) {
}

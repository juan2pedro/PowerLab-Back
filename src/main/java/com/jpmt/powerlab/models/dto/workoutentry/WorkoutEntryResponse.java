package com.jpmt.powerlab.models.dto.workoutentry;

public record WorkoutEntryResponse(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer displayOrder,
        String notes,
        Long sourceTemplateId
) {
}

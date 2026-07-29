package com.jpmt.powerlab.models.dto.workoutentry;

public record WorkoutEntryResponse(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer displayOrder,
        Boolean isWarmup,
        String notes,
        Long sourceTemplateId
) {
}

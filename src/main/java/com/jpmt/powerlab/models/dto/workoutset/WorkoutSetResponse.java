package com.jpmt.powerlab.models.dto.workoutset;

import java.time.OffsetDateTime;

public record WorkoutSetResponse(
        Long id,
        Long workoutEntryId,
        Integer sequenceNumber,
        Integer reps,
        Double weight,
        Double rpeValue,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.SetType;

import java.time.OffsetDateTime;

public record WorkoutSetResponse(
        Long id,
        Long workoutEntryId,
        Integer sequenceNumber,
        Integer reps,
        Double weight,
        Double rpeValue,
        SetType type,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
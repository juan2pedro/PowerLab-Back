package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.IntensityType;

import java.time.OffsetDateTime;

public record WorkoutSetResponse(
        Long id,
        Long workoutEntryId,
        Integer sequenceNumber,
        Integer reps,
        Double weight,
        IntensityType intensityType,
        Double intensityValue,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
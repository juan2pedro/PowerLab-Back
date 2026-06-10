package com.jpmt.strengthlab.models.dto.workoutset;

import com.jpmt.strengthlab.models.domain.IntensityType;

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
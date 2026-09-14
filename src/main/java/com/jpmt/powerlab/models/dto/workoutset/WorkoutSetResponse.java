package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.GearClass;
import com.jpmt.powerlab.models.domain.SetType;
import com.jpmt.powerlab.models.domain.SupportGear;

import java.time.OffsetDateTime;
import java.util.Set;

public record WorkoutSetResponse(
        Long id,
        Long workoutEntryId,
        Integer sequenceNumber,
        Integer reps,
        Double weight,
        Double rpeValue,
        SetType type,
        GearClass gearClass,
        Set<SupportGear> usedGear,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
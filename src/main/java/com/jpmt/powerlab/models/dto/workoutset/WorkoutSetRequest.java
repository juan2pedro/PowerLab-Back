package com.jpmt.powerlab.models.dto.workoutset;

import com.jpmt.powerlab.models.domain.GearClass;
import com.jpmt.powerlab.models.domain.SetType;
import com.jpmt.powerlab.models.domain.SupportGear;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record WorkoutSetRequest(
        @NotNull
        @Min(1)
        Integer sequenceNumber,
        @NotNull
        @Min(1)
        Integer reps,
        @Min(0)
        Double weight,
        @Min(0)
        @Max(11)
        Double rpeValue,
        GearClass gearClass,
        Set<SupportGear> usedGear,
        SetType type
) {
}

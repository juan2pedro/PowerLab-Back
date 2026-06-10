package com.jpmt.powerlab.models.dto.workoutentry;

import jakarta.validation.constraints.Min;

public record WorkoutEntryUpdateRequest(
        @Min(value = 1, message = "actualSets must be at least 1")
        Integer actualSets,
        @Min(value = 1, message = "actualReps must be at least 1")
        Integer actualReps,
        Double actualPlateWeight,
        String actualRirOrRpe,
        Boolean isWarmup,
        String notes
) {
}

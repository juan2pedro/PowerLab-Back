package com.jpmt.powerlab.models.dto.workoutentry;

public record WorkoutEntryResponse(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer actualSets,
        Integer actualReps,
        Double actualPlateWeight,
        String actualRirOrRpe,
        Boolean isWarmup,
        String notes
) {
}

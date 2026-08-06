package com.jpmt.powerlab.models.dto.workoutentry;

public record WorkoutEntryUpdateRequest(
        Integer displayOrder,
        String notes
) {
}

package com.jpmt.powerlab.models.dto.exercise;

import java.time.LocalDate;

public record LastExerciseResponse(
        Long exerciseId,
        Double weight,
        Integer reps,
        Integer rpe,
        LocalDate performedAt,
        Long workoutSessionId
) {
}

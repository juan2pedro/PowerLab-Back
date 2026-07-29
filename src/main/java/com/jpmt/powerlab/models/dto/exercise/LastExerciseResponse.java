package com.jpmt.powerlab.models.dto.exercise;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record LastExerciseResponse(
        Long exerciseId,
        Double weight,
        Integer reps,
        @Min(0)
        @Max(11) Double rpe,
        LocalDate performedAt,
        Long workoutSessionId
) {
}

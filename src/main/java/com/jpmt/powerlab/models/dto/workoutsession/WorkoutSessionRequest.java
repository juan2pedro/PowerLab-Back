package com.jpmt.powerlab.models.dto.workoutsession;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WorkoutSessionRequest(
        @NotNull(message = "date is required")
        LocalDate date,
        Long trainingSessionTemplateId
) {
}

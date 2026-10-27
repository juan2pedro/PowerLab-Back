package com.jpmt.powerlab.models.dto.workoutsession;

import com.jpmt.powerlab.models.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WorkoutSessionStatusRequest(
        @NotNull(message = "status is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
}

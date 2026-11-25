package com.jpmt.powerlab.models.dto.bodyweight;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Registro de un pesaje corporal")
public record BodyweightRequest(
        @NotNull
        @Schema(description = "Fecha del pesaje (ISO 8601)", example = "2026-11-24")
        LocalDate date,

        @NotNull
        @Positive
        @Schema(description = "Peso corporal en kg", example = "82.5")
        Double weight,

        @Size(max = 255)
        @Schema(description = "Notas opcionales", example = "En ayunas")
        String notes
) {
}

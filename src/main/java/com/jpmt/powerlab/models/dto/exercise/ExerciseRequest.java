package com.jpmt.powerlab.models.dto.exercise;

import com.jpmt.powerlab.models.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para creación/actualización de Exercise
 */
public record ExerciseRequest(
        @Schema(description = "Nombre del ejercicio", example = "Sentadilla con barra")
        @NotBlank(message = "baseName is required")
        String baseName,
        @Schema(description = "Patrón principal del ejercicio", example = "SQUAT")
        @NotNull(message = "mainPattern is required")
        MainPattern mainPattern,
        Implement implement,
        Setup setup,
        Stance stance,
        Grip grip,
        String tempo,
        Integer pauseSeconds,
        HeightMode heightMode,
        Integer heightLength,
        String style,
        String bar,
        String resistance,
        LoadingMethod loadingMethod,
        LoadingUnit loadingUnit,
        String equipment,
        String notes
) {
}
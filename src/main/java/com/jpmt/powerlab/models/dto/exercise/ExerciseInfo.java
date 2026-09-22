package com.jpmt.powerlab.models.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vista resumida de un ejercicio para incluir embebido en otras respuestas
 * (entries de un workout, sets pautados de una plantilla, etc.).
 * Mantén aquí solo los campos que el frontend necesita pintar in-line.
 */
public record ExerciseInfo(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String baseName,
        String mainPattern,
        String implement,
        String setup,
        String stance,
        String grip
) {
}

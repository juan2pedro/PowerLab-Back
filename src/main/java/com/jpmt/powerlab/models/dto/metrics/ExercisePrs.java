package com.jpmt.powerlab.models.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Récords de un ejercicio.
 *
 * @param repMaxes          mejor peso conseguido (no fallado) por número de reps.
 * @param failedRepMaxes    mejor peso fallado por número de reps. Se devuelve aunque pese menos que
 *                          el válido; separado de {@code repMaxes} para no pisar nunca un récord
 *                          conseguido. El front decide qué pintar.
 * @param estimatedRepMaxes RM estimados por reps a partir de {@code bestE1rm}. Vacío si no hay e1RM.
 */
public record ExercisePrs(
        Long exerciseId,
        String label,
        @Schema(description = "RM configurado que se usa para resolver los %1RM de las plantillas. Null si no está configurado.")
        Double referenceMax,
        PrSetInfo heaviestSet,
        Double bestE1rm,
        PrSetInfo bestE1rmSet,
        @Deprecated
        @Schema(deprecated = true, description = "Redundante con failedRepMaxes[reps=1]. Usar failedRepMaxes.")
        PrSetInfo bestFailedSingle,
        List<RepMax> repMaxes,
        List<RepMax> failedRepMaxes,
        List<EstimatedRepMax> estimatedRepMaxes
) {}

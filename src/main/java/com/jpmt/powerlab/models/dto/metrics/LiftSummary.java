package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.models.domain.MainPattern;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Mejores marcas de un movimiento de competición.
 *
 * @param referenceMax     RM configurado para resolver los %1RM de las plantillas. Null si no está
 *                         configurado (solo tiene valor cuando el movimiento es un comp lift configurado).
 * @param bestWeight       peso más alto movido a cualquier número de repeticiones. No es un RM:
 *                         un 200x5 gana a un 190x1. Excluye intentos fallados.
 * @param bestSingle       RM real: single más pesado conseguido (reps == 1, no fallado).
 * @param bestFailedSingle single más pesado fallado (RPE > 10). Es el techo actual, no un récord.
 */
public record LiftSummary(
        MainPattern lift,
        Long exerciseId,
        String label,
        boolean compLiftConfigured,
        @Schema(description = "RM configurado que se usa para resolver los %1RM de las plantillas. Null si no está configurado.")
        Double referenceMax,
        Double bestWeight,
        Double bestE1rm,
        Double xBodyweight,
        PrSetInfo bestSingle,
        PrSetInfo bestFailedSingle
) {}

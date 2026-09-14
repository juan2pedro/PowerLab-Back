package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.models.domain.MainPattern;

/**
 * Mejores marcas de un movimiento de competición.
 *
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
        Double bestWeight,
        Double bestE1rm,
        Double xBodyweight,
        PrSetInfo bestSingle,
        PrSetInfo bestFailedSingle
) {}

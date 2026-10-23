package com.jpmt.powerlab.models.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Mejor marca a un número fijo de repeticiones. En {@code failedRepMaxes} el peso corresponde a un
 * intento fallado (RPE &gt; 10); en {@code repMaxes}, a un levantamiento conseguido.
 */
public record RepMax(
        int reps,
        double weight,
        @Schema(description = "RPE con el que se consiguió/falló la marca. Null si no se registró.")
        Double rpe,
        LocalDate date
) {}

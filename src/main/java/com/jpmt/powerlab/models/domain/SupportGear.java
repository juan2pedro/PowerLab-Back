package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Piezas de apoyo que se pueden combinar libremente en un mismo set.
 * Distinto de {@link GearClass}, que es una clasificación excluyente.
 */
@Schema(enumAsRef = true)
public enum SupportGear {
    BELT, SLEEVES, WRAPS, STRAPS, SUIT
}

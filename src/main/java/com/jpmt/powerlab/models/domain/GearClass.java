package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Clase de equipamiento con la que se ejecuta un set, mutuamente excluyente.
 * Distinto de {@link SupportGear}, que son piezas acumulables.
 */
@Schema(enumAsRef = true)
public enum GearClass {
    RAW, FULL, COMPETITION
}

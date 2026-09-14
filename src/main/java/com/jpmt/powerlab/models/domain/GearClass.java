package com.jpmt.powerlab.models.domain;

/**
 * Clase de equipamiento con la que se ejecuta un set, mutuamente excluyente.
 * Distinto de {@link SupportGear}, que son piezas acumulables.
 */
public enum GearClass {
    RAW, FULL, COMPETITION
}

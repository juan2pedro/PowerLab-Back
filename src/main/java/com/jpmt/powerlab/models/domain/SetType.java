package com.jpmt.powerlab.models.domain;

/**
 * Intención con la que se ejecuta una serie. Sustituye a {@code WorkoutEntry.isWarmup}, que
 * obligaba a partir un mismo ejercicio en dos entries para poder registrar la rampa.
 * <p>
 * Es una etiqueta declarada por el atleta, no una medición: el número de levantamientos (NL)
 * y las zonas de intensidad se derivan de la carga, no de este campo.
 */
public enum SetType {
    /** Aproximación general con cargas bajas, previa a la rampa. */
    WARMUP,
    /** Series progresivas de acercamiento a la carga de trabajo. */
    RAMP,
    /** Serie de trabajo prescrita. */
    WORKING,
    /** Volumen posterior a la serie top, con carga reducida. */
    BACKOFF
}

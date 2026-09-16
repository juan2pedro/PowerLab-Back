package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.models.domain.SetType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
@Schema(enumAsRef = true)

/**
 * Eje de <em>intención</em> del filtro de métricas: qué clase de series entran en el cálculo.
 * Es independiente del eje de intensidad (%1RM), que se deriva de la carga y no del esquema.
 */
public enum SetFilter {
    WORKING_SETS(EnumSet.of(SetType.WORKING, SetType.BACKOFF)),
    WARMUP_SETS(EnumSet.of(SetType.WARMUP, SetType.RAMP)),
    RAMP_SETS(EnumSet.of(SetType.RAMP)),
    ALL_SETS(EnumSet.allOf(SetType.class));

    private final Set<SetType> setTypes;

    SetFilter(Set<SetType> setTypes) {
        this.setTypes = Collections.unmodifiableSet(setTypes);
    }

    /**
     * Nunca devuelve vacío: las queries de métricas usan {@code IN :setTypes} sin la rama
     * {@code :param IS NULL}, frágil en Hibernate 6 cuando la colección es null.
     */
    public Set<SetType> setTypes() {
        return setTypes;
    }
}

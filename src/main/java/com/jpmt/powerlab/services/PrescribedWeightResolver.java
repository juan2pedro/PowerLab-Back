package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.TrainingSetTemplate;

import java.util.Collection;
import java.util.Map;

/**
 * Resuelve a kg la prescripción porcentual de los sets de una plantilla (peso de barra y
 * resistencia de banda) a partir del RM de referencia del atleta.
 *
 * El RM depende del atleta, por eso {@code athleteId} es un parámetro: hoy los callers pasan
 * una constante (MVP sin usuarios), pero la interfaz queda lista para multi-atleta.
 */
public interface PrescribedWeightResolver {

    /**
     * @return mapa {@code idDelSet → prescripción resuelta}. Solo incluye los sets con algún
     * valor resuelto; se omiten los que no tienen nada que resolver (sin intensidad porcentual
     * ni bandas, o cuya fuente de referencia aún no está implementada).
     */
    Map<Long, ResolvedPrescription> resolveAll(Collection<TrainingSetTemplate> sets, Long athleteId);

    /**
     * Valores en kg derivados del RM de referencia. No se persisten (el RM cambia con el tiempo);
     * son info para la UI. Cualquier campo puede ser {@code null} si no aplica o no se pudo resolver.
     *
     * @param weightKg peso de barra resuelto desde la intensidad porcentual.
     * @param bandMinKg resistencia de banda (mínimo del rango pautado).
     * @param bandMaxKg resistencia de banda (máximo del rango pautado).
     */
    record ResolvedPrescription(Double weightKg, Double bandMinKg, Double bandMaxKg) {
        public boolean isEmpty() {
            return weightKg == null && bandMinKg == null && bandMaxKg == null;
        }
    }
}

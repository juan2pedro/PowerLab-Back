package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.IntensityType;
import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.domain.ReferenceSource;
import com.jpmt.powerlab.models.domain.TrainingSetTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PrescribedWeightResolverImpl implements PrescribedWeightResolver {

    private final OneRepMaxProvider oneRepMaxProvider;
    private final CompLiftProvider compLiftProvider;

    public PrescribedWeightResolverImpl(OneRepMaxProvider oneRepMaxProvider,
                                        CompLiftProvider compLiftProvider) {
        this.oneRepMaxProvider = oneRepMaxProvider;
        this.compLiftProvider = compLiftProvider;
    }

    @Override
    public Map<Long, ResolvedPrescription> resolveAll(Collection<TrainingSetTemplate> sets, Long athleteId) {
        Map<Long, ResolvedPrescription> result = new HashMap<>();
        for (TrainingSetTemplate set : sets) {
            if (set.getId() == null) {
                continue;
            }
            Optional<Double> base = referenceBase(set, athleteId);
            ResolvedPrescription resolved = new ResolvedPrescription(
                    resolveWeight(set, base),
                    resolveBand(set.getTargetResistanceMinPct(), base),
                    resolveBand(set.getTargetResistanceMaxPct(), base));
            if (!resolved.isEmpty()) {
                result.put(set.getId(), resolved);
            }
        }
        return result;
    }

    /** Peso de barra desde la intensidad porcentual. No se resuelve si hay peso fijo explícito. */
    private Double resolveWeight(TrainingSetTemplate set, Optional<Double> base) {
        if (set.getTargetWeight() != null || set.getTargetIntensityType() != IntensityType.PERCENTAGE_1RM) {
            return null;
        }
        return base.map(b -> applyPct(b, set.getTargetIntensityMin())).orElse(null);
    }

    /** Resistencia de banda: mismo origen (RM de referencia), otro porcentaje. Firmado = reverse. */
    private Double resolveBand(Integer pct, Optional<Double> base) {
        if (pct == null) {
            return null;
        }
        return base.map(b -> applyPct(b, pct.doubleValue())).orElse(null);
    }

    /** Cuenta pura compartida por barra y bandas. */
    private Double applyPct(double base, Double pct) {
        return pct == null ? null : base * pct / 100.0;
    }

    /**
     * Valor base en kg sobre el que se aplican los porcentajes. Hoy la única fuente implementada es
     * el 1RM de referencia del atleta; cuando entren otras (ERM, top del día, ejecución exacta de la
     * semana anterior, por slot code) este método pasa a un switch por fuente sin tocar {@link #applyPct}.
     */
    private Optional<Double> referenceBase(TrainingSetTemplate set, Long athleteId) {
        return referenceExerciseId(set, athleteId)
                .flatMap(exerciseId -> oneRepMaxProvider.referenceMax(athleteId, exerciseId));
    }

    /**
     * El ejercicio cuyo 1RM es la referencia, según el {@link ReferenceSource} del set:
     * {@code SELF} = el propio ejercicio pautado; {@code COMPETITION} (por defecto) = el comp-lift
     * del atleta para el {@code mainPattern} del ejercicio (sumo o convencional en el peso muerto).
     */
    private Optional<Long> referenceExerciseId(TrainingSetTemplate set, Long athleteId) {
        if (set.getExercise() == null) {
            return Optional.empty();
        }
        if (set.getReferenceSource() == ReferenceSource.SELF) {
            return Optional.ofNullable(set.getExercise().getId());
        }
        MainPattern lift = set.getExercise().getMainPattern();
        if (lift == null) {
            return Optional.empty();
        }
        return compLiftProvider.compLiftExerciseId(athleteId, lift);
    }
}

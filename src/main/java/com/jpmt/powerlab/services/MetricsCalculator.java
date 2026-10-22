package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Exercise;
import com.jpmt.powerlab.models.domain.Sex;
import com.jpmt.powerlab.models.domain.WorkoutSet;

public interface MetricsCalculator {

    /**
     * Carga efectiva del set según el LoadMode del ejercicio (peso externo o corporal).
     * Null si no es computable (LoadMode.NONE, o EXTERNAL sin peso registrado).
     */
    Double effectiveLoad(WorkoutSet set, Exercise exercise, Long athleteId);

    /**
     * 1RM estimado (Epley, ajustado por RPE si existe). Null si no aplica.
     */
    Double e1rm(WorkoutSet set, Exercise exercise, Long athleteId);

    boolean isHardSet(WorkoutSet set);

    boolean isFailedSet(WorkoutSet set);

    /**
     * True si el set cuenta como levantamiento conseguido: tiene peso registrado y no fue un
     * intento fallado. Un set sin RPE se considera válido.
     */
    boolean isValidLift(WorkoutSet set);

    /**
     * True si la carga efectiva del set depende de un dato no medido directamente
     * (peso corporal o resistencia acomodada por bandas/cadenas).
     */
    boolean isEstimated(WorkoutSet set, Exercise exercise);

    double dots(double totalKg, double bodyweightKg, Sex sex);

    double ipfGlPoints(double totalKg, double bodyweightKg, Sex sex);
}

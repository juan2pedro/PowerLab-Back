package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Fórmulas de cálculo de métricas de rendimiento.
 *
 * <p>effectiveLoad (loadMode null se trata como EXTERNAL):
 * <ul>
 *   <li>EXTERNAL: weight (null si no hay peso registrado)</li>
 *   <li>BODYWEIGHT: bodyweightKg</li>
 *   <li>BODYWEIGHT_PLUS: bodyweightKg + (weight ?? 0)</li>
 *   <li>BODYWEIGHT_MINUS: max(0, bodyweightKg - (weight ?? 0))</li>
 *   <li>NONE: null</li>
 * </ul>
 * La tensión de bandas/cadenas no se suma (no hay dato de magnitud en WorkoutSet, solo en la
 * plantilla); su sola presencia marca el set como {@link #isEstimated}.
 *
 * <p>e1rm: null si measurementType != REPS, carga no computable o &lt;= 0, reps &lt; 1, o
 * repeticiones efectivas &gt; 12 (fuera del rango fiable de estas fórmulas). Las repeticiones
 * efectivas incorporan el RIR derivado del RPE antes de aplicar la fórmula elegida vía
 * {@code metrics.e1rm-formula} (EPLEY por defecto):
 * <pre>
 *     rir     = rpe == null ? null : max(0, 10 - rpe)
 *     effReps = reps + (rir ?? 0)      // sin RPE se asume fallo (marca estimated)
 *
 *     EPLEY:    e1rm = load * (1 + effReps / 30.0)
 *     BRZYCKI:  e1rm = load * 36.0 / (37.0 - effReps)
 *     LOMBARDI: e1rm = load * effReps ^ 0.10
 * </pre>
 *
 * <p>DOTS: {@code total * 500 / (A + B·bw + C·bw² + D·bw³ + E·bw⁴)}, bw clamp [40,210] hombres
 * / [40,150] mujeres.
 *
 * <p>IPF GL (raw/classic): {@code total * 100 / (A - B·e^(-C·bw))}.
 */
@Service
public class MetricsCalculatorImpl implements MetricsCalculator {

    private static final int MAX_EFFECTIVE_REPS = 12;
    private static final double FAILED_SET_RPE_THRESHOLD = 10.0;

    // DOTS
    private static final double DOTS_MALE_A = -307.75076;
    private static final double DOTS_MALE_B = 24.0900756;
    private static final double DOTS_MALE_C = -0.1918759221;
    private static final double DOTS_MALE_D = 0.0007391293;
    private static final double DOTS_MALE_E = -0.000001093;

    private static final double DOTS_FEMALE_A = -57.96288;
    private static final double DOTS_FEMALE_B = 13.6175032;
    private static final double DOTS_FEMALE_C = -0.1126655495;
    private static final double DOTS_FEMALE_D = 0.0005158568;
    private static final double DOTS_FEMALE_E = -0.0000010706;

    private static final double DOTS_MALE_BW_MIN = 40, DOTS_MALE_BW_MAX = 210;
    private static final double DOTS_FEMALE_BW_MIN = 40, DOTS_FEMALE_BW_MAX = 150;

    // IPF GL (raw/classic)
    private static final double IPF_MALE_A = 1199.72839;
    private static final double IPF_MALE_B = 1025.18162;
    private static final double IPF_MALE_C = 0.00921;

    private static final double IPF_FEMALE_A = 610.32796;
    private static final double IPF_FEMALE_B = 1045.59282;
    private static final double IPF_FEMALE_C = 0.03048;

    private final BodyweightProvider bodyweightProvider;
    private final double hardSetRpeThreshold;
    private final E1rmFormula e1rmFormula;

    public MetricsCalculatorImpl(BodyweightProvider bodyweightProvider,
                                  @Value("${metrics.hard-set-rpe-threshold:8.0}") double hardSetRpeThreshold,
                                  @Value("${metrics.e1rm-formula:EPLEY}") E1rmFormula e1rmFormula) {
        this.bodyweightProvider = bodyweightProvider;
        this.hardSetRpeThreshold = hardSetRpeThreshold;
        this.e1rmFormula = e1rmFormula;
    }

    @Override
    public Double effectiveLoad(WorkoutSet set, Exercise exercise, Long athleteId) {
        LoadMode loadMode = exercise.getLoadMode() == null ? LoadMode.EXTERNAL : exercise.getLoadMode();
        Double weight = set.getWeight();

        return switch (loadMode) {
            case EXTERNAL -> weight;
            case BODYWEIGHT -> bodyweightKg(set, athleteId);
            case BODYWEIGHT_PLUS -> bodyweightKg(set, athleteId) + (weight == null ? 0 : weight);
            case BODYWEIGHT_MINUS -> Math.max(0, bodyweightKg(set, athleteId) - (weight == null ? 0 : weight));
            case NONE -> null;
        };
    }

    private Double bodyweightKg(WorkoutSet set, Long athleteId) {
        return bodyweightProvider.bodyweightKg(athleteId, set.getWorkoutEntry().getSession().getDate());
    }

    @Override
    public Double e1rm(WorkoutSet set, Exercise exercise, Long athleteId) {
        if (exercise.getMeasurementType() != null && exercise.getMeasurementType() != MeasurementType.REPS) {
            return null;
        }
        Double load = effectiveLoad(set, exercise, athleteId);
        if (load == null || load <= 0) {
            return null;
        }
        Integer reps = set.getReps();
        if (reps == null || reps < 1) {
            return null;
        }
        Double rpe = set.getRpeValue();
        double rir = rpe == null ? 0 : Math.max(0, 10 - rpe);
        double effectiveReps = reps + rir;
        if (effectiveReps > MAX_EFFECTIVE_REPS) {
            return null;
        }
        return switch (e1rmFormula) {
            case EPLEY -> load * (1 + effectiveReps / 30.0);
            case BRZYCKI -> load * 36.0 / (37.0 - effectiveReps);
            case LOMBARDI -> load * Math.pow(effectiveReps, 0.10);
        };
    }

    @Override
    public boolean isHardSet(WorkoutSet set) {
        Double rpe = set.getRpeValue();
        return rpe != null && rpe >= hardSetRpeThreshold;
    }

    @Override
    public boolean isFailedSet(WorkoutSet set) {
        Double rpe = set.getRpeValue();
        return rpe != null && rpe > FAILED_SET_RPE_THRESHOLD;
    }

    @Override
    public boolean isValidLift(WorkoutSet set) {
        return set.getWeight() != null && !isFailedSet(set);
    }

    @Override
    public boolean isEstimated(WorkoutSet set, Exercise exercise) {
        LoadMode loadMode = exercise.getLoadMode();
        boolean usesBodyweight = loadMode == LoadMode.BODYWEIGHT
                || loadMode == LoadMode.BODYWEIGHT_PLUS
                || loadMode == LoadMode.BODYWEIGHT_MINUS;
        boolean usesAccommodatingResistance = exercise.getAccommodatingResistance() != null
                && exercise.getAccommodatingResistance() != AccommodatingResistance.NONE;
        return usesBodyweight || usesAccommodatingResistance;
    }

    @Override
    public double dots(double totalKg, double bodyweightKg, Sex sex) {
        double bw = clamp(bodyweightKg,
                sex == Sex.MALE ? DOTS_MALE_BW_MIN : DOTS_FEMALE_BW_MIN,
                sex == Sex.MALE ? DOTS_MALE_BW_MAX : DOTS_FEMALE_BW_MAX);
        double a = sex == Sex.MALE ? DOTS_MALE_A : DOTS_FEMALE_A;
        double b = sex == Sex.MALE ? DOTS_MALE_B : DOTS_FEMALE_B;
        double c = sex == Sex.MALE ? DOTS_MALE_C : DOTS_FEMALE_C;
        double d = sex == Sex.MALE ? DOTS_MALE_D : DOTS_FEMALE_D;
        double e = sex == Sex.MALE ? DOTS_MALE_E : DOTS_FEMALE_E;
        double denominator = a + b * bw + c * Math.pow(bw, 2) + d * Math.pow(bw, 3) + e * Math.pow(bw, 4);
        return totalKg * 500 / denominator;
    }

    @Override
    public double ipfGlPoints(double totalKg, double bodyweightKg, Sex sex) {
        double a = sex == Sex.MALE ? IPF_MALE_A : IPF_FEMALE_A;
        double b = sex == Sex.MALE ? IPF_MALE_B : IPF_FEMALE_B;
        double c = sex == Sex.MALE ? IPF_MALE_C : IPF_FEMALE_C;
        double denominator = a - b * Math.exp(-c * bodyweightKg);
        return totalKg * 100 / denominator;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

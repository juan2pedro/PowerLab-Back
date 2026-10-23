package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MetricsCalculatorTest {

    private static final double BODYWEIGHT = 88.5;
    private static final double DELTA = 0.01;

    @Mock
    private BodyweightProvider bodyweightProvider;

    private MetricsCalculatorImpl calculator;

    @BeforeEach
    void setUp() {
        lenient().when(bodyweightProvider.bodyweightKg(any(), any())).thenReturn(BODYWEIGHT);
        calculator = new MetricsCalculatorImpl(bodyweightProvider, 8.0, E1rmFormula.EPLEY);
    }

    private WorkoutSet buildSet(Double weight, Integer reps, Double rpe) {
        WorkoutSession session = WorkoutSession.builder().id(1L).date(LocalDate.now()).build();
        Exercise exercise = Exercise.builder().id(1L).baseName("Test").mainPattern(MainPattern.SQUAT).build();
        WorkoutEntry entry = WorkoutEntry.builder().id(1L).session(session).exercise(exercise).build();
        return WorkoutSet.builder().id(1L).workoutEntry(entry).weight(weight).reps(reps).rpeValue(rpe).build();
    }

    private Exercise exerciseWith(LoadMode loadMode, MeasurementType measurementType, AccommodatingResistance resistance) {
        return Exercise.builder().id(1L).baseName("Test").mainPattern(MainPattern.SQUAT)
                .loadMode(loadMode).measurementType(measurementType).accommodatingResistance(resistance).build();
    }

    // ============================================================================
    // e1rm
    // ============================================================================

    @Test
    void e1rm_epley_withRpe() {
        WorkoutSet set = buildSet(100.0, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        Double e1rm = calculator.e1rm(set, exercise, 1L);

        assertEquals(100 * (1 + 7 / 30.0), e1rm, DELTA);
    }

    @Test
    void e1rm_withoutRpe_assumesFailure() {
        WorkoutSet set = buildSet(100.0, 5, null);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        Double e1rm = calculator.e1rm(set, exercise, 1L);

        assertEquals(100 * (1 + 5 / 30.0), e1rm, DELTA);
    }

    @Test
    void e1rm_failedSet_countsOnlyCompletedRepsAtRir0() {
        // 5 reps con FALLO = las 4 completadas a RPE 10 (RIR 0): la 5ª no salió y no infla el e1RM.
        WorkoutSet set = buildSet(100.0, 5, 11.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        Double e1rm = calculator.e1rm(set, exercise, 1L);

        assertEquals(100 * (1 + 4 / 30.0), e1rm, DELTA);
    }

    @Test
    void e1rm_failedSingle_returnsNull() {
        // Un single fallado no tiene repeticiones completadas: no estima nada.
        WorkoutSet set = buildSet(100.0, 1, 11.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        assertNull(calculator.e1rm(set, exercise, 1L));
    }

    @Test
    void weightForReps_epley_isInverseOfE1rm() {
        double e1rm = 100 * (1 + 5 / 30.0);
        assertEquals(100.0, calculator.weightForReps(e1rm, 5), DELTA);
    }

    @Test
    void weightForReps_brzycki_isInverseOfE1rm() {
        MetricsCalculatorImpl brzycki = new MetricsCalculatorImpl(bodyweightProvider, 8.0, E1rmFormula.BRZYCKI);
        double e1rm = 100 * 36.0 / (37.0 - 5);
        assertEquals(100.0, brzycki.weightForReps(e1rm, 5), DELTA);
    }

    @Test
    void weightForReps_lombardi_isInverseOfE1rm() {
        MetricsCalculatorImpl lombardi = new MetricsCalculatorImpl(bodyweightProvider, 8.0, E1rmFormula.LOMBARDI);
        double e1rm = 100 * Math.pow(5, 0.10);
        assertEquals(100.0, lombardi.weightForReps(e1rm, 5), DELTA);
    }

    @Test
    void e1rm_effectiveRepsTooHigh_returnsNull() {
        WorkoutSet set = buildSet(100.0, 12, 1.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        assertNull(calculator.e1rm(set, exercise, 1L));
    }

    @Test
    void e1rm_nonRepsMeasurement_returnsNull() {
        WorkoutSet set = buildSet(100.0, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.TIME, null);

        assertNull(calculator.e1rm(set, exercise, 1L));
    }

    @Test
    void e1rm_loadModeNone_returnsNull() {
        WorkoutSet set = buildSet(100.0, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.NONE, MeasurementType.REPS, null);

        assertNull(calculator.e1rm(set, exercise, 1L));
    }

    @Test
    void e1rm_noWeight_returnsNull() {
        WorkoutSet set = buildSet(null, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        assertNull(calculator.e1rm(set, exercise, 1L));
    }

    @Test
    void e1rm_bodyweightPlus() {
        WorkoutSet set = buildSet(20.0, 8, 9.0);
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT_PLUS, MeasurementType.REPS, null);

        Double e1rm = calculator.e1rm(set, exercise, 1L);

        double expectedLoad = BODYWEIGHT + 20.0;
        assertEquals(expectedLoad * (1 + 9 / 30.0), e1rm, DELTA);
    }

    @Test
    void e1rm_formulaBrzycki() {
        MetricsCalculatorImpl brzyckiCalculator = new MetricsCalculatorImpl(bodyweightProvider, 8.0, E1rmFormula.BRZYCKI);
        WorkoutSet set = buildSet(100.0, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        Double e1rm = brzyckiCalculator.e1rm(set, exercise, 1L);

        assertEquals(100 * 36.0 / (37.0 - 7), e1rm, DELTA);
    }

    @Test
    void e1rm_formulaLombardi() {
        MetricsCalculatorImpl lombardiCalculator = new MetricsCalculatorImpl(bodyweightProvider, 8.0, E1rmFormula.LOMBARDI);
        WorkoutSet set = buildSet(100.0, 5, 8.0);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        Double e1rm = lombardiCalculator.e1rm(set, exercise, 1L);

        assertEquals(100 * Math.pow(7, 0.10), e1rm, DELTA);
    }

    // ============================================================================
    // effectiveLoad
    // ============================================================================

    @Test
    void effectiveLoad_external() {
        WorkoutSet set = buildSet(150.0, 3, null);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        assertEquals(150.0, calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_external_nullWeight_returnsNull() {
        WorkoutSet set = buildSet(null, 3, null);
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, null);

        assertNull(calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_bodyweight() {
        WorkoutSet set = buildSet(null, 10, null);
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT, MeasurementType.REPS, null);

        assertEquals(BODYWEIGHT, calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_bodyweightPlus() {
        WorkoutSet set = buildSet(20.0, 10, null);
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT_PLUS, MeasurementType.REPS, null);

        assertEquals(BODYWEIGHT + 20.0, calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_bodyweightMinus() {
        WorkoutSet set = buildSet(20.0, 10, null);
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT_MINUS, MeasurementType.REPS, null);

        assertEquals(BODYWEIGHT - 20.0, calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_bodyweightMinus_clampsAtZero() {
        WorkoutSet set = buildSet(1000.0, 10, null);
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT_MINUS, MeasurementType.REPS, null);

        assertEquals(0.0, calculator.effectiveLoad(set, exercise, 1L));
    }

    @Test
    void effectiveLoad_none_returnsNull() {
        WorkoutSet set = buildSet(100.0, 10, null);
        Exercise exercise = exerciseWith(LoadMode.NONE, MeasurementType.REPS, null);

        assertNull(calculator.effectiveLoad(set, exercise, 1L));
    }

    // ============================================================================
    // hard / failed sets
    // ============================================================================

    @Test
    void hardSet_belowThreshold_isFalse() {
        assertFalse(calculator.isHardSet(buildSet(100.0, 5, 7.9)));
    }

    @Test
    void hardSet_atThreshold_isTrue() {
        assertTrue(calculator.isHardSet(buildSet(100.0, 5, 8.0)));
    }

    @Test
    void failedSet_atTen_isFalse() {
        assertFalse(calculator.isFailedSet(buildSet(100.0, 5, 10.0)));
    }

    @Test
    void failedSet_aboveTen_isTrue() {
        assertTrue(calculator.isFailedSet(buildSet(100.0, 5, 10.5)));
    }

    @Test
    void validLift_atTen_isTrue() {
        assertTrue(calculator.isValidLift(buildSet(100.0, 1, 10.0)));
    }

    @Test
    void validLift_aboveTen_isFalse() {
        assertFalse(calculator.isValidLift(buildSet(100.0, 1, 11.0)));
    }

    @Test
    void validLift_withoutRpe_isTrue() {
        assertTrue(calculator.isValidLift(buildSet(100.0, 1, null)));
    }

    @Test
    void validLift_withoutWeight_isFalse() {
        assertFalse(calculator.isValidLift(buildSet(null, 1, 8.0)));
    }

    // ============================================================================
    // isEstimated
    // ============================================================================

    @Test
    void isEstimated_bodyweightLoadMode_isTrue() {
        Exercise exercise = exerciseWith(LoadMode.BODYWEIGHT, MeasurementType.REPS, null);

        assertTrue(calculator.isEstimated(buildSet(null, 5, 8.0), exercise));
    }

    @Test
    void isEstimated_accommodatingResistance_isTrue() {
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, AccommodatingResistance.BANDS);

        assertTrue(calculator.isEstimated(buildSet(100.0, 5, 8.0), exercise));
    }

    @Test
    void isEstimated_plainExternal_isFalse() {
        Exercise exercise = exerciseWith(LoadMode.EXTERNAL, MeasurementType.REPS, AccommodatingResistance.NONE);

        assertFalse(calculator.isEstimated(buildSet(100.0, 5, 8.0), exercise));
    }

    // ============================================================================
    // DOTS / IPF GL (valores de referencia calculados independientemente)
    // ============================================================================

    @Test
    void dots_male() {
        assertEquals(391.30, calculator.dots(600.0, 88.5, Sex.MALE), 0.05);
    }

    @Test
    void dots_male_higherTotal_higherScore() {
        assertEquals(423.90, calculator.dots(650.0, 88.5, Sex.MALE), 0.05);
    }

    @Test
    void dots_female() {
        assertEquals(332.56, calculator.dots(300.0, 60.0, Sex.FEMALE), 0.05);
    }

    @Test
    void dots_male_bodyweightClampedBelowMin() {
        double clamped = calculator.dots(300.0, 35.0, Sex.MALE);
        double atMin = calculator.dots(300.0, 40.0, Sex.MALE);
        assertEquals(atMin, clamped, DELTA);
    }

    @Test
    void ipfGl_male() {
        assertEquals(80.43, calculator.ipfGlPoints(600.0, 88.5, Sex.MALE), 0.05);
    }

    @Test
    void ipfGl_male_higherTotal_higherScore() {
        assertEquals(87.13, calculator.ipfGlPoints(650.0, 88.5, Sex.MALE), 0.05);
    }

    @Test
    void ipfGl_female() {
        assertEquals(67.81, calculator.ipfGlPoints(300.0, 60.0, Sex.FEMALE), 0.05);
    }
}

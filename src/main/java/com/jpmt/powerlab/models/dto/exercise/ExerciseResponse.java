package com.jpmt.powerlab.models.dto.exercise;

import com.jpmt.powerlab.models.domain.*;

/**
 * DTO response para Exercise
 */
public record ExerciseResponse(
        Long id,
        String baseName,
        MainPattern mainPattern,
        Implement implement,
        Setup setup,
        Stance stance,
        Grip grip,
        HeightMode heightMode,
        Double heightValue,
        AccommodatingResistance accommodatingResistance,
        Boolean unilateral,
        MeasurementType measurementType,
        LoadMode loadMode,
        String notes
) {
}


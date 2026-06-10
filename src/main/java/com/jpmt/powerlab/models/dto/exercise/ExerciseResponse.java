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
        String tempo,
        Integer pauseSeconds,
        HeightMode heightMode,
        Integer heightLength,
        String style,
        String bar,
        String resistance,
        LoadingMethod loadingMethod,
        LoadingUnit loadingUnit,
        String equipment,
        String notes
) {
}


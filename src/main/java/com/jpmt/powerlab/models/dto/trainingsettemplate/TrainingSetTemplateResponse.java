package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.ExerciseInfo;

import java.util.Set;

public record TrainingSetTemplateResponse(
        Long id,
        SlotCode slotCode,
        Integer targetSets,
        Integer targetRepsMin,
        Integer targetRepsMax,
        Boolean amrap,
        Double targetWeight,
        Double targetIntensityMin,
        Double targetIntensityMax,
        IntensityType targetIntensityType,
        SlotCode referenceSlotCode,
        ReferenceSource referenceSource,
        Integer  targetResistanceTopPct,
        Integer targetResistanceBottomPct,
        String tempo,
        GearClass prescribedGearClass,
        Set<SupportGear> prescribedGear,
        Long referenceExerciseId,
        String rest,
        String notes,
        Integer displayOrder,
        ExerciseInfo exercise
) {
}

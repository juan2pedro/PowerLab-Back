package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.GearClass;
import com.jpmt.powerlab.models.domain.IntensityType;
import com.jpmt.powerlab.models.domain.SlotCode;
import com.jpmt.powerlab.models.domain.SupportGear;
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

package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.ExerciseInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record TrainingSetTemplateResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        SlotCode slotCode,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer targetSets,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer targetRepsMin,
        Integer targetRepsMax,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Set<SupportGear> prescribedGear,
        Long referenceExerciseId,
        String rest,
        String notes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer displayOrder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ExerciseInfo exercise
) {
}

package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record TrainingSetTemplateRequest(
        @NotNull(message = "targetSets is required")
        @Min(value = 1, message = "targetSets must be at least 1")
        Integer targetSets,
        @NotNull(message = "targetRepsMin is required")
        @Min(value = 1, message = "targetRepsMin must be at least 1")
        Integer targetRepsMin,
        @Min(value = 1, message = "targetRepsMax must be at least 1")
        Integer targetRepsMax,
        @NotNull(message = "amrap is required")
        Boolean amrap,
        SlotCode slotCode,
        Double targetWeight,
        Double targetIntensityMin,
        Double targetIntensityMax,
        IntensityType targetIntensityType,
        SlotCode referenceSlotCode,
        ReferenceSource referenceSource,
        Integer targetResistanceMinPct,
        Integer targetResistanceMaxPct,
        @Pattern(regexp = "\\d-\\d-[\\dX]-\\d(\\|\\d-\\d-[\\dX]-\\d)*")
         String tempo,
        GearClass prescribedGearClass,
        Set<SupportGear> prescribedGear,
        Long referenceExerciseId,
        String rest,
        String notes,
        @NotNull(message = "displayOrder is required")
        Integer displayOrder,
        @NotNull(message = "exerciseId is required")
        Long exerciseId
) {
}

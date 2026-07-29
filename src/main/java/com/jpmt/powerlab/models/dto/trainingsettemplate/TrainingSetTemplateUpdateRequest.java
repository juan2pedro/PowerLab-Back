package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.Equipment;
import com.jpmt.powerlab.models.domain.IntensityType;
import com.jpmt.powerlab.models.domain.SlotCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record TrainingSetTemplateUpdateRequest(
        @NotNull(message = "id is required for update")
        Long id,
        @Min(value = 1, message = "targetSets must be at least 1")
        Integer targetSets,
        @Min(value = 1, message = "targetReps must be at least 1")
        Integer targetRepsMin,
        Integer targetRepsMax,
        Boolean amrap,
        SlotCode slotCode,
        @Pattern(regexp = "\\d-\\d-[\\dX]-\\d")
        String tempo,
        Double targetWeight,
        Double targetIntensityMin,
        Double targetIntensityMax,
        IntensityType targetIntensityType,
        Integer targetResistanceTopPct,
        Integer targetResistanceBottomPct,
        Set<Equipment> prescribedEquipment,
        Long referenceExerciseId,
        String rest,
        String notes,
        @Min(value = 1, message = "displayOrder must be at least 1")
        Integer displayOrder,
        Long exerciseId
) {
}

package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.IntensityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TrainingSetTemplateRequest(
        @NotNull(message = "targetSets is required")
        @Min(value = 1, message = "targetSets must be at least 1")
        Integer targetSets,
        @NotNull(message = "targetReps is required")
        @Min(value = 1, message = "targetReps must be at least 1")
        Integer targetReps,
        Double targetWeight,
        Integer targetIntensity,
        IntensityType targetIntensityType,
        String rest,
        String notes,
        @NotNull(message = "displayOrder is required")
        Integer displayOrder,
        @NotNull(message = "exerciseId is required")
        Long exerciseId
) {
}

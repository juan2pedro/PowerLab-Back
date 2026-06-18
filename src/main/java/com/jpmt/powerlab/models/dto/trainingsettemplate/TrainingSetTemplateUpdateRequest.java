package com.jpmt.powerlab.models.dto.trainingsettemplate;

import com.jpmt.powerlab.models.domain.IntensityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TrainingSetTemplateUpdateRequest(
        @NotNull(message = "id is required for update")
        Long id,
        @Min(value = 1, message = "targetSets must be at least 1")
        Integer targetSets,
        @Min(value = 1, message = "targetReps must be at least 1")
        Integer targetReps,
        Double targetWeight,
        Integer targetIntensity,
        IntensityType targetIntensityType,
        String rest,
        String notes,
        @Min(value = 1, message = "displayOrder must be at least 1")
        Integer displayOrder,
        Long exerciseId
) {
}

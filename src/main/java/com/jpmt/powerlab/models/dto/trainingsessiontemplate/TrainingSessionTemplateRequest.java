package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import com.jpmt.powerlab.models.domain.ConjugatedDayType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainingSessionTemplateRequest(
        @NotBlank(message = "blockName is required")
        String blockName,
        @NotNull(message = "weekNumber is required")
        @Min(value = 1, message = "weekNumber must be at least 1")
        Integer weekNumber,
        @NotNull(message = "dayInWeek is required")
        @Min(value = 1, message = "dayInWeek must be between 1 and 7")
        @Max(value = 7, message = "dayInWeek must be between 1 and 7")
        Integer dayInWeek,
        @NotNull(message = "conjugateDayType is required")
        ConjugatedDayType conjugatedDayType,
        String notes
) {
}

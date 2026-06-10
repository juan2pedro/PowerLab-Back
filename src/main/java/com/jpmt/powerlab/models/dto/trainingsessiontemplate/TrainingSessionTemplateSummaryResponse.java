package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

public record TrainingSessionTemplateSummaryResponse(
        Long id,
        String blockName,
        Integer weekNumber,
        Integer dayInWeek
) {
}


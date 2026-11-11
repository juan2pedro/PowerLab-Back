package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import com.jpmt.powerlab.models.domain.Block;

public record TrainingSessionTemplateSummaryResponse(
        Long id,
        Block block,
        Integer weekNumber,
        Integer dayIndex
) {
}


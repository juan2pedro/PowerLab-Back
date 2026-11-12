package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import com.jpmt.powerlab.models.dto.block.BlockResponse;

public record TrainingSessionTemplateSummaryResponse(
        Long id,
        BlockResponse block,
        Integer weekNumber,
        Integer dayIndex
) {
}


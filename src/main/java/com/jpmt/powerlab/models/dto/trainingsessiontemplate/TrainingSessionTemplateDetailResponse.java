package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import com.jpmt.powerlab.models.domain.ConjugatedDayType;
import com.jpmt.powerlab.models.dto.block.BlockResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;

import java.util.List;


public record TrainingSessionTemplateDetailResponse(
        Long id,
        BlockResponse block,
        Integer weekNumber,
        Integer dayIndex,
        ConjugatedDayType conjugatedDayType,
        String notes,
        List<TrainingSetTemplateResponse> setTemplates
) {

}

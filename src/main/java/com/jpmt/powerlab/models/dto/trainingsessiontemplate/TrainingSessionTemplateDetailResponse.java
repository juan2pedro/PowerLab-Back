package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import com.jpmt.powerlab.models.domain.ConjugatedDayType;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;

import java.util.List;


public record TrainingSessionTemplateDetailResponse(
        Long id,
        String blockName,
        Integer weekNumber,
        Integer dayInWeek,
        ConjugatedDayType conjugatedDayType,
        String notes,
        List<TrainingSetTemplateResponse> setTemplates
) {

}

package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateUpdateRequest;

import java.util.List;

public interface TrainingSessionService {
    // Session template
    List<TrainingSessionTemplateSummaryResponse> findAllSession();

    List<TrainingSessionTemplateSummaryResponse> findAllSessionByBlockNameAndWeekNumber(String blockName, Integer weekNumber);

    TrainingSessionTemplateDetailResponse saveSession(TrainingSessionTemplateRequest trainingSessionTemplate);

    TrainingSessionTemplateDetailResponse updateSession(Long id, TrainingSessionTemplateRequest trainingSessionTemplate);

    void deleteSessionById(Long id);

    TrainingSessionTemplateDetailResponse findSessionById(Long id);

    // Set template
    TrainingSetTemplateResponse saveSet(Long sessionId, TrainingSetTemplateRequest trainingSetTemplate);

    TrainingSetTemplateResponse updateSet(Long id, TrainingSetTemplateUpdateRequest trainingSetTemplate);

    void deleteSetById(Long id);

    TrainingSetTemplateResponse findSetById(Long id);

    List<TrainingSetTemplateResponse> findBySessionIdWithExercise(Long sessionId);
}

package com.jpmt.powerlab.models.dto.workoutsession;

import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;

import java.time.LocalDate;
import java.util.List;

public record WorkoutDayResponse(
        Long workoutId,
        LocalDate date,
        TemplateInfo template,
        List<Entry> entries
) {
    public record TemplateInfo(
            Long id,
            String blockName,
            Integer weekNumber,
            Integer dayInWeek,
            String conjugateDayType,
            String notes,
            List <TrainingSetTemplateResponse> setTemplates
    ) {}

    public record Entry(
            Long entryId,
            ExerciseResponse exercise,
            Target target,          // opcional: si viene de template
            String notes,
            Boolean isWarmup,
            List<Set> sets
    ) {}

    public record Target(
            Integer targetSets,
            Integer targetReps,
            Double targetWeight,
            String targetIntensityType,   // RIR/RPE/PERCENTAGE
            String targetIntensityValue,  // "8" o "2" o "75" (simple)
            String rest
    ) {}

    public record Set(
            Long id,
            Integer sequenceNumber,
            Integer reps,
            Double weight,
            String intensityType,   // RIR/RPE/PERCENTAGE
            Double intensityValue,
            String createdAt,
            String updatedAt
    ) {}
}
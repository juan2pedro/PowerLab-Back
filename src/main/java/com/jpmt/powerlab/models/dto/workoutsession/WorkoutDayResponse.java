package com.jpmt.powerlab.models.dto.workoutsession;

import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record WorkoutDayResponse(
        @NotNull
        Long workoutId,
        LocalDate date,
        Status status,
        TemplateInfo template,
        List<WorkoutDayEntry> entries
) {
    public record TemplateInfo(
            Long id,
            String blockName,
            Integer weekNumber,
            Integer dayIndex,
            String conjugateDayType,
            String notes,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List <TrainingSetTemplateResponse> setTemplates
    ) {}
    public record WorkoutDayEntry(
            Long entryId,
            Integer displayOrder,
            ExerciseResponse exercise,
            WorkoutDayTarget target,          // opcional: si viene de template
            String notes,
            List<WorkoutDaySet> sets,
            Long sourceTemplateId
    ) {}
    public record WorkoutDayTarget(
            Integer targetSets,
            Integer targetReps,
            Double targetWeight,
            IntensityType targetIntensityType,   // RIR/RPE/PERCENTAGE...
            Double targetIntensityValue,  // "8" o "2" o "75" (simple)
            String rest
    ) {}

    public record WorkoutDaySet(
            Long id,
            Integer sequenceNumber,
            Integer reps,
            Double weight,
            Double rpeValue,
            SetType type,
            GearClass gearClass,
            Set<SupportGear> usedGear,
            String createdAt,
            String updatedAt
    ) {}
}
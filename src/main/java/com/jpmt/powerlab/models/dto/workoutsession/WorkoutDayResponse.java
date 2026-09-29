package com.jpmt.powerlab.models.dto.workoutsession;

import com.jpmt.powerlab.models.domain.GearClass;
import com.jpmt.powerlab.models.domain.SetType;
import com.jpmt.powerlab.models.domain.SupportGear;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record WorkoutDayResponse(
        @NotNull
        Long workoutId,
        LocalDate date,
        TemplateInfo template,
        List<Entry> entries
) {
    public record TemplateInfo(
            Long id,
            String blockName,
            Integer weekNumber,
            Integer dayIndex,
            String conjugateDayType,
            String notes,
            @NotNull
            List <TrainingSetTemplateResponse> setTemplates
    ) {}
    @Schema(name = "WorkoutDayEntry")
    public record Entry(
            Long entryId,
            Integer displayOrder,
            ExerciseResponse exercise,
            Target target,          // opcional: si viene de template
            String notes,
            List<Set> sets,
            Long sourceTemplateId
    ) {}
    @Schema(name = "WorkoutDayTarget")
    public record Target(
            Integer targetSets,
            Integer targetReps,
            Double targetWeight,
            String targetIntensityType,   // RIR/RPE/PERCENTAGE
            String targetIntensityValue,  // "8" o "2" o "75" (simple)
            String rest
    ) {}
    @Schema(name = "WorkoutDaySet")
    public record Set(
            Long id,
            Integer sequenceNumber,
            Integer reps,
            Double weight,
            Double rpeValue,
            SetType type,
            GearClass gearClass,
            SupportGear supportGear,
            String createdAt,
            String updatedAt
    ) {}
}
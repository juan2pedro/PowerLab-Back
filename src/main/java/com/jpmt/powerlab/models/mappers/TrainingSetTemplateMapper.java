package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.Exercise;
import com.jpmt.powerlab.models.domain.TrainingSetTemplate;
import com.jpmt.powerlab.models.dto.exercise.ExerciseInfo;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import com.jpmt.powerlab.models.dto.trainingsettemplate.TrainingSetTemplateUpdateRequest;
import com.jpmt.powerlab.services.PrescribedWeightResolver.ResolvedPrescription;
import org.mapstruct.*;

import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrainingSetTemplateMapper {
    @Mapping(source = "exercise", target = "exercise")
    @Mapping(source = "referenceExercise.id", target = "referenceExerciseId")
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    @Mapping(target = "targetWeight", expression = "java(resolvedTargetWeight(entity, resolved))")
    @Mapping(target = "resolvedBandMinKg", expression = "java(resolvedBand(entity, resolved, true))")
    @Mapping(target = "resolvedBandMaxKg", expression = "java(resolvedBand(entity, resolved, false))")
    @Named("toResponseWithResolvedWeights")
    TrainingSetTemplateResponse toResponseWithResolvedWeights(
            TrainingSetTemplate entity,
            @Context Map<Long, ResolvedPrescription> resolved
    );

    default TrainingSetTemplateResponse toResponse(TrainingSetTemplate entity) {
        return toResponseWithResolvedWeights(entity, Map.of());
    }

    default Double resolvedTargetWeight(TrainingSetTemplate entity, Map<Long, ResolvedPrescription> resolved) {
        if (entity.getTargetWeight() != null) return entity.getTargetWeight();
        ResolvedPrescription p = prescription(entity, resolved);
        return p != null ? p.weightKg() : null;
    }

    default Double resolvedBand(TrainingSetTemplate entity, Map<Long, ResolvedPrescription> resolved, boolean min) {
        ResolvedPrescription p = prescription(entity, resolved);
        if (p == null) return null;
        return min ? p.bandMinKg() : p.bandMaxKg();
    }

    private ResolvedPrescription prescription(TrainingSetTemplate entity, Map<Long, ResolvedPrescription> resolved) {
        return entity.getId() == null ? null : resolved.get(entity.getId());
    }

    ExerciseInfo toExerciseInfo(Exercise entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionTemplate", ignore = true)
    @Mapping(target = "exercise.id", source = "exerciseId")
    @Mapping(target = "referenceExercise", ignore = true)
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    TrainingSetTemplate toEntity(TrainingSetTemplateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionTemplate", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "referenceExercise", ignore = true)
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    void updateEntityFromRequest(
            TrainingSetTemplateUpdateRequest request,
            @MappingTarget TrainingSetTemplate entity
    );
}

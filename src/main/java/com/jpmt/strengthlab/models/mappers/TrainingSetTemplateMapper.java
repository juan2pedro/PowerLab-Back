package com.jpmt.strengthlab.models.mappers;

import com.jpmt.strengthlab.models.domain.Exercise;
import com.jpmt.strengthlab.models.domain.TrainingSetTemplate;
import com.jpmt.strengthlab.models.dto.exercise.ExerciseInfo;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateRequest;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateResponse;
import com.jpmt.strengthlab.models.dto.trainingsettemplate.TrainingSetTemplateUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrainingSetTemplateMapper {
    @Mapping(source = "exercise", target = "exercise")
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    TrainingSetTemplateResponse toResponse(TrainingSetTemplate entity);

    ExerciseInfo toExerciseInfo(Exercise entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionTemplate", ignore = true)
    @Mapping(target = "exercise.id", source = "exerciseId")
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    TrainingSetTemplate toEntity(TrainingSetTemplateRequest dto);

    @Mapping(target = "sessionTemplate", ignore = true)
    @Mapping(target = "exercise.id", source = "exerciseId")
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    TrainingSetTemplate toUpdateEntity(TrainingSetTemplateUpdateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionTemplate", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(source = "targetIntensityType", target = "targetIntensityType")
    void updateEntityFromRequest(
            TrainingSetTemplateUpdateRequest request,
            @MappingTarget TrainingSetTemplate entity
    );
}

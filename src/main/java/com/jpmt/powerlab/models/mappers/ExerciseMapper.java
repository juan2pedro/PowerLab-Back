package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.Exercise;
import com.jpmt.powerlab.models.dto.exercise.ExerciseRequest;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ExerciseMapper {
    ExerciseResponse toResponse(Exercise entity);

    @Mapping(target = "id", ignore = true)
    Exercise toEntity(ExerciseRequest dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(
            ExerciseRequest request,
            @MappingTarget Exercise entity
    );

}

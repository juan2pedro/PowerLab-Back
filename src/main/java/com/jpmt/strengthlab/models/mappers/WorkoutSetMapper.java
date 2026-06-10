package com.jpmt.strengthlab.models.mappers;

import com.jpmt.strengthlab.models.domain.WorkoutSet;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkoutSetMapper {

    @Mapping(source = "workoutEntry.id", target = "workoutEntryId")
    @Mapping(source = "sequenceNumber", target = "sequenceNumber")
    WorkoutSetResponse toResponse(WorkoutSet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workoutEntry", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "sequenceNumber", target = "sequenceNumber")
    WorkoutSet toEntity(WorkoutSetRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workoutEntry", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "sequenceNumber", target = "sequenceNumber")
    void updateEntityFromRequest(WorkoutSetRequest request, @MappingTarget WorkoutSet entity);
}
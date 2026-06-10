package com.jpmt.strengthlab.models.mappers;

import com.jpmt.strengthlab.models.domain.WorkoutEntry;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryCreateRequest;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryResponse;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkoutEntryMapper {
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(source = "exercise.id", target = "exerciseId")
    WorkoutEntryResponse toResponse(WorkoutEntry dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sets", ignore = true)
    @Mapping(target = "session.id", source = "sessionId")
    @Mapping(target = "exercise.id", source = "exerciseId")
    WorkoutEntry toEntity(WorkoutEntryCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "sets", ignore = true)
    void updateEntityFromRequest(
            WorkoutEntryUpdateRequest request,
            @MappingTarget WorkoutEntry entity
    );
}

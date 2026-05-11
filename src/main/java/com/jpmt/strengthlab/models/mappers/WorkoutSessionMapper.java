package com.jpmt.strengthlab.models.mappers;

import com.jpmt.strengthlab.models.domain.*;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import org.mapstruct.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {com.jpmt.strengthlab.models.mappers.TrainingSetTemplateMapper.class})
public interface WorkoutSessionMapper {
    WorkoutSession toEntity(WorkoutSessionRequest dto);

    @Mapping(source = "trainingSessionTemplate.id", target = "trainingSessionTemplateId")
    @Mapping(source = "entries", target = "entryIds", qualifiedByName = "mapEntriesToIds")
    WorkoutSessionDetailResponse toDetailResponse(WorkoutSession entity);

    @Mapping(source = "trainingSessionTemplate.id", target = "trainingSessionTemplateId")
    @Mapping(source = "entries", target = "entryIds", qualifiedByName = "mapEntriesToIds")
    WorkoutSessionSummaryResponse toSummaryResponse(WorkoutSession entity);

    @Named("mapEntriesToIds")
    default List<Long> mapEntriesToIds(List<WorkoutEntry> entries) {
        if (entries == null) {
            return Collections.emptyList();
        }
        return entries.stream()
                .map(WorkoutEntry::getId)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(
            WorkoutSessionRequest request,
            @MappingTarget WorkoutSession entity
    );

   @Mapping(source = "id", target = "workoutId")
   @Mapping(source = "trainingSessionTemplate", target = "template")
   WorkoutDayResponse toDayResponse(WorkoutSession entity);

   @Mapping(source = "conjugatedDayType", target = "conjugateDayType")
   @Mapping(source = "setTemplates", target = "setTemplates")
   WorkoutDayResponse.TemplateInfo toTemplateInfo(TrainingSessionTemplate entity);

   @Mapping(source = "id", target = "entryId")
   WorkoutDayResponse.Entry toEntry(WorkoutEntry entity);

   WorkoutDayResponse.Set toSet(WorkoutSet entity);

   WorkoutDayResponse.ExerciseInfo toExerciseInfo(Exercise entity);

   default String map(OffsetDateTime value) {
       return value != null ? value.toString() : null;
   }
}

package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.TrainingSessionTemplate;
import com.jpmt.powerlab.models.domain.WorkoutEntry;
import com.jpmt.powerlab.models.domain.WorkoutSession;
import com.jpmt.powerlab.models.domain.WorkoutSet;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {com.jpmt.powerlab.models.mappers.TrainingSetTemplateMapper.class})
public interface WorkoutSessionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingSessionTemplate", ignore = true)
    @Mapping(target = "entries", ignore = true)
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

   @Mapping(source = "id", target = "workoutId")
   @Mapping(source = "trainingSessionTemplate", target = "template")
   WorkoutDayResponse toDayResponse(WorkoutSession entity);

   @Mapping(source = "conjugatedDayType", target = "conjugateDayType")
   @Mapping(source = "trainingSets", target = "setTemplates")
   WorkoutDayResponse.TemplateInfo toTemplateInfo(TrainingSessionTemplate entity);

   @Mapping(source = "id", target = "entryId")
   @Mapping(source = "exercise", target = "exercise")
   @Mapping(target = "target", ignore = true) // TODO: correlar con TrainingSetTemplate (FK directa o lookup exercise+order)
   WorkoutDayResponse.Entry toEntry(WorkoutEntry entity);

   @Mapping(source = "sequenceNumber", target = "sequenceNumber")
   WorkoutDayResponse.Set toSet(WorkoutSet entity);

   default String map(OffsetDateTime value) {
       return value != null ? value.toString() : null;
   }
}

package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.powerlab.services.PrescribedWeightResolver.ResolvedPrescription;
import org.mapstruct.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
   WorkoutDayResponse toDayResponse(WorkoutSession entity, @Context Map<Long, ResolvedPrescription> resolved);

   @Mapping(source = "conjugatedDayType", target = "conjugateDayType")
   @Mapping(source = "trainingSets", target = "setTemplates", qualifiedByName = "toResponseWithResolvedWeights")
   WorkoutDayResponse.TemplateInfo toTemplateInfo(TrainingSessionTemplate entity, @Context Map<Long, ResolvedPrescription> resolved);

   @Mapping(source = "id", target = "entryId")
   @Mapping(source = "exercise", target = "exercise")
   @Mapping(source = "sourceTemplate", target = "target")
   @Mapping(source = "sourceTemplate.id", target = "sourceTemplateId")
   WorkoutDayResponse.WorkoutDayEntry toEntry(WorkoutEntry entity);

   @Mapping(source = "targetRepsMin", target = "targetReps")
   @Mapping(source = "targetIntensityMin", target = "targetIntensityValue")
   WorkoutDayResponse.WorkoutDayTarget toTarget(TrainingSetTemplate entity);

   @Mapping(source = "sequenceNumber", target = "sequenceNumber")
   WorkoutDayResponse.WorkoutDaySet toSet(WorkoutSet entity);

   default String map(OffsetDateTime value) {
       return value != null ? value.toString() : null;
   }
}

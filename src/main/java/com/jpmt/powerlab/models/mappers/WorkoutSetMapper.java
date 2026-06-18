package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.WorkoutSet;
import com.jpmt.powerlab.models.dto.exercise.LastExerciseResponse;
import com.jpmt.powerlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.powerlab.models.dto.workoutset.WorkoutSetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
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

    @Mapping(source = "workoutEntry.exercise.id", target = "exerciseId")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "reps", target = "reps")
    @Mapping(source = "intensityValue", target = "rpe")
    @Mapping(source = "workoutEntry.session.date", target = "performedAt")
    @Mapping(source = "workoutEntry.session.id", target = "workoutSessionId")
    LastExerciseResponse toLastExerciseResponse(WorkoutSet workoutSet);

    default LastExerciseResponse toLastExerciseResponseDefault(WorkoutSet workoutSet) {
        if (workoutSet == null) {
            return null;
        }
        return new LastExerciseResponse(
                workoutSet.getWorkoutEntry().getExercise().getId(),
                workoutSet.getWeight(),
                workoutSet.getReps(),
                workoutSet.getIntensityValue(),
                workoutSet.getWorkoutEntry().getSession().getDate(),
                workoutSet.getWorkoutEntry().getSession().getId()
        );
    }
}
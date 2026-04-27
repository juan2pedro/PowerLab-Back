package com.jpmt.strengthlab.services;

import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryCreateRequest;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryResponse;
import com.jpmt.strengthlab.models.dto.workoutentry.WorkoutEntryUpdateRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.strengthlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetRequest;
import com.jpmt.strengthlab.models.dto.workoutset.WorkoutSetResponse;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutService {
    // workoutSessions
    WorkoutSessionDetailResponse saveWorkoutSession(WorkoutSessionRequest session);

    WorkoutSessionDetailResponse createWorkoutFromTemplate(Long templeteId, WorkoutSessionRequest session);

    List<WorkoutSessionSummaryResponse> findAllWorkoutSessionsByDateRange(LocalDate from,LocalDate to);

    WorkoutSessionDetailResponse findWorkoutById(Long id);

    void deleteWorkoutById(Long id);
    WorkoutDayResponse findFullDayById(Long id);

    WorkoutDayResponse findFullDayByDate(LocalDate date);

    // workoutEntries
    WorkoutEntryResponse saveWorkoutEntry(WorkoutEntryCreateRequest entry);

    WorkoutEntryResponse updateEntry(Long id, WorkoutEntryUpdateRequest entry);

    void deleteWorkoutEntryById(Long id);

    // workoutSets
    List<WorkoutSetResponse> findAllWorkoutSets(Long entryId);

    WorkoutSetResponse saveWorkoutSet(Long entryId, @Valid WorkoutSetRequest workoutSet);

    WorkoutSetResponse updateWorkoutSet(Long id, @Valid WorkoutSetRequest workoutSet);

    void deleteWorkoutSetById(Long id);

}

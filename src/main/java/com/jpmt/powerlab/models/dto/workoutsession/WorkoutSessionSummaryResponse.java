package com.jpmt.powerlab.models.dto.workoutsession;

import com.jpmt.powerlab.models.domain.Status;

import java.time.LocalDate;
import java.util.List;

public record WorkoutSessionSummaryResponse(
        Long id,
        LocalDate date,
        Long trainingSessionTemplateId,
        Status status,
        List<Long> entryIds
) {
}


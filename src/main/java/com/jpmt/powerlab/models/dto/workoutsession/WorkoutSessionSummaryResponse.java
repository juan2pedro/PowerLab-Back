package com.jpmt.powerlab.models.dto.workoutsession;

import java.time.LocalDate;
import java.util.List;

public record WorkoutSessionSummaryResponse(
        Long id,
        LocalDate date,
        Long trainingSessionTemplateId,
        List<Long> entryIds
) {
}


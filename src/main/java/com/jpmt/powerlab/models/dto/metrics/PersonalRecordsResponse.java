package com.jpmt.powerlab.models.dto.metrics;

import java.time.LocalDate;
import java.util.List;

public record PersonalRecordsResponse(
        Long athleteId,
        LocalDate from,
        LocalDate to,
        List<ExercisePrs> exercises
) {}

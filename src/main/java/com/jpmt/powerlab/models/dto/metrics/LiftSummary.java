package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.models.domain.MainPattern;

public record LiftSummary(
        MainPattern lift,
        Long exerciseId,
        String label,
        boolean compLiftConfigured,
        Double bestWeight,
        Double bestE1rm,
        Double xBodyweight
) {}

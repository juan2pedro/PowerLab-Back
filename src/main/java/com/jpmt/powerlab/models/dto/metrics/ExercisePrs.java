package com.jpmt.powerlab.models.dto.metrics;

import java.util.List;

public record ExercisePrs(
        Long exerciseId,
        String label,
        PrSetInfo heaviestSet,
        Double bestE1rm,
        PrSetInfo bestE1rmSet,
        List<RepMax> repMaxes
) {}

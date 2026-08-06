package com.jpmt.powerlab.models.dto.metrics;

import java.util.List;

public record SbdSummary(
        Double bodyweightKg,
        Double bestRealTotal,
        Double bestE1rmTotal,
        Double dots,
        Double dotsE1rm,
        Double ipfGl,
        Double ipfGlE1rm,
        List<LiftSummary> lifts
) {}

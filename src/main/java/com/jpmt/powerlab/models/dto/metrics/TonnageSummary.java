package com.jpmt.powerlab.models.dto.metrics;

import java.util.List;

public record TonnageSummary(
        double lifetimeTonnageKg,
        List<TonnageMilestone> milestones,
        TonnageMilestone nextMilestone,
        Double pctToNextMilestone
) {}

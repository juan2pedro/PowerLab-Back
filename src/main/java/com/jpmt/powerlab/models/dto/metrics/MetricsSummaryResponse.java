package com.jpmt.powerlab.models.dto.metrics;

public record MetricsSummaryResponse(
        Long athleteId,
        SbdSummary sbd,
        ConsistencySummary consistency,
        TonnageSummary tonnage
) {}

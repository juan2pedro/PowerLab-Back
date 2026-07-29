package com.jpmt.powerlab.models.dto.metrics;

import java.time.LocalDate;
import java.util.List;

public record MetricsResponse(
        Long athleteId,
        MetricsGroupBy groupBy,
        MetricsPeriod period,
        LocalDate from,
        LocalDate to,
        List<MetricsSeries> series
) {}
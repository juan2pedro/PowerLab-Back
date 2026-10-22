package com.jpmt.powerlab.models.dto.metrics;

import com.jpmt.powerlab.models.domain.MeasurementType;

import java.util.List;

public record MetricsSeries(
        String key,              // exerciseId | pattern | "all"
        String label,            // "Back Squat (comp)"
        MeasurementType measure, // unidad de quantity
        boolean estimated,       // algún punto con banda/bw estimado
        List<MetricsPoint> points
) {}
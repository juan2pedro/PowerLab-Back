package com.jpmt.powerlab.models.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)

public enum MetricsPeriod {
    NONE,
    DAY,
    WEEK,
    MONTH,
    YEAR,
    BLOCK
}

package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum MainPattern {
    NONE,
    SQUAT,
    BENCH,
    DEADLIFT,
    VERTICAL_PRESS,
    HORIZONTAL_PRESS,
    HORIZONTAL_PULL,
    VERTICAL_PULL,
    OLY,
    ACCESSORY
}


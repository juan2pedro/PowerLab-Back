package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Stance {
    NARROW, MEDIUM, WIDE, SUMO,CONVENTIONAL,NONE,LARSEN, PARALYMPIC, SPLIT, REVERSE_LUNGE
}

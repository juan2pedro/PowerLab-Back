package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Grip {
    CLOSE, MEDIUM, WIDE, SNATCH, CLEAN, NEUTRAL, PRONATED, SUPINATED, MIXED, NONE, HOOK
}

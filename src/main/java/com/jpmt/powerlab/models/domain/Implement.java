package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Implement {
    BARBELL, DUMBBELL, KETTLEBELL, TRAP_BAR, SAFETY_BAR, CAMBERED_BAR,
    SWISS_BAR, PLATES, CABLE, MACHINE, SMITH, RINGS, BODY_WEIGHT,
    LANDMINE, PULLUP_BAR, BAND, LEVERAGE_MACHINE, WEIGHTED,EX_BARBELL,OTHER,FAT_BAR,SPIDER_BAR
}
package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum HeightMode {
    NONE, DEFICIT, SUPERHABIT, BLOCK, PINS, BOARD
}

package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Setup {
    STANDARD, LOWBAR, HIGHBAR, FRONT, ZERCHER, BOX
}

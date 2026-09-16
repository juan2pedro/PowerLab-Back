package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ConjugatedDayType {
    ME_UPPER,
    ME_LOWER,
    DE_UPPER,
    DE_LOWER,
    RE_UPPER,
    RE_LOWER,
    NONE
}

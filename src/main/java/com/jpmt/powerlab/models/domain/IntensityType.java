package com.jpmt.powerlab.models.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum IntensityType {
    RIR,
    RPE,
    PERCENTAGE_1RM,
    PERCENTAGE_ERM,
    PERCENTAGE_DIALY_TOP;

    @JsonCreator
    public static IntensityType fromValue(String value) {
        if (value == null) return null;
        return IntensityType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}

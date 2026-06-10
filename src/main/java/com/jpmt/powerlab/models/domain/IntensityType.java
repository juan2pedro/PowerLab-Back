package com.jpmt.powerlab.models.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IntensityType {
    RIR,
    RPE,
    PERCENTAGE;

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

package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum LoadMode {
    EXTERNAL,BODYWEIGHT,BODYWEIGHT_PLUS,BODYWEIGHT_MINUS,NONE
}

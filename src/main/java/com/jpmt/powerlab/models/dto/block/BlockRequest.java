package com.jpmt.powerlab.models.dto.block;

import io.swagger.v3.oas.annotations.media.Schema;

public record BlockRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        String notes
) {

}

package com.jpmt.powerlab.models.dto.bodyweight;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Pesaje corporal registrado")
public record BodyweightResponse(
        Long id,
        LocalDate date,
        Double weight,
        String notes
) {
}

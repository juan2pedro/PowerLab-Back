package com.jpmt.powerlab.models.dto.trainingsessiontemplate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetOrderRequest(
        @NotNull Long id,
        @NotNull @Min(1)
        Integer displayOrder) {}
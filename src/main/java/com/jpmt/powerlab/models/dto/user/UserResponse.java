package com.jpmt.powerlab.models.dto.user;

import com.jpmt.powerlab.models.domain.Visibility;

import java.time.LocalDate;

public record UserResponse(
        String username,
        LocalDate dateOfBirth,
        Visibility defaultPrVisibility
) {


}

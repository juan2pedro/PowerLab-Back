package com.jpmt.powerlab.models.dto.user;

import com.jpmt.powerlab.models.domain.Visibility;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String email,
        String username,
        boolean coach,
        LocalDate dateOfBirth,
        Visibility defaultPrVisibility
) {


}

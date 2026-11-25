package com.jpmt.powerlab.models.dto.user;

import com.jpmt.powerlab.security.ExistByEmail;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        @ExistByEmail
        String email,
        @NotBlank
        String password,
        @NotBlank
        String username,
        @NotBlank
        String sex,

        String dateOfBirth
) {
}

package com.jpmt.powerlab.models.dto.user;

public record AuthResponse(
        String token,
        String email
) {
}

package com.jpmt.powerlab.models.dto.user;

public record LoginRequest(
        String email,
        String password
) {
}

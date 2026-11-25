package com.jpmt.powerlab.models.dto.coaching;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Invitacion de un coach a un atleta por email")
public record CoachingInviteRequest(
        @NotBlank
        @Email
        @Schema(description = "Email del atleta a invitar", example = "atleta@example.com")
        String athleteEmail
) {
}

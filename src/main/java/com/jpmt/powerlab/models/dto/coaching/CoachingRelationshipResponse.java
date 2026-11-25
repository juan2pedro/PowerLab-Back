package com.jpmt.powerlab.models.dto.coaching;

import com.jpmt.powerlab.models.domain.CoachingInitiator;
import com.jpmt.powerlab.models.domain.CoachingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Relacion de coaching entre un coach y un atleta")
public record CoachingRelationshipResponse(
        Long id,
        Long coachId,
        String coachUsername,
        Long athleteId,
        String athleteUsername,
        CoachingStatus status,
        CoachingInitiator initiatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime respondedAt
) {
}

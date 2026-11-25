package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.models.dto.coaching.CoachingInviteRequest;
import com.jpmt.powerlab.models.dto.coaching.CoachingRelationshipResponse;

import java.util.List;

public interface CoachingService {

    /** El coach actual invita a un atleta por email. Crea la relacion en PENDING. */
    CoachingRelationshipResponse invite(CoachingInviteRequest request);

    /** El atleta actual acepta una invitacion PENDING -> ACTIVE. */
    CoachingRelationshipResponse accept(Long id);

    /** El atleta actual rechaza una invitacion PENDING -> DECLINED. */
    CoachingRelationshipResponse decline(Long id);

    /** Coach o atleta de la relacion revoca un vinculo ACTIVE -> REVOKED. */
    CoachingRelationshipResponse revoke(Long id);

    /**
     * Invitaciones/relaciones del usuario actual.
     * @param sent   true = las que envio como coach; false = las recibidas como atleta.
     * @param status filtro opcional por estado (null = todas).
     */
    List<CoachingRelationshipResponse> invitations(boolean sent, CoachingStatus status);

    /** Atletas ACTIVE del coach actual. */
    List<CoachingRelationshipResponse> myAthletes();

    /** Coach ACTIVE del atleta actual (puede no haber). */
    CoachingRelationshipResponse myCoach();
}

package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ForbiddenException;
import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.repositories.CoachingRelationshipRepository;
import com.jpmt.powerlab.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final CurrentUserProvider currentUserProvider;
    private final CoachingRelationshipRepository coachingRepository;

    public AuthorizationServiceImpl(CurrentUserProvider currentUserProvider,
                                    CoachingRelationshipRepository coachingRepository) {
        this.currentUserProvider = currentUserProvider;
        this.coachingRepository = coachingRepository;
    }

    @Override
    public Long resolveReadableAthlete(Long requestedAthleteId) {
        Long currentUserId = currentUserProvider.currentUserId();
        if (requestedAthleteId == null || requestedAthleteId.equals(currentUserId)) {
            return currentUserId;
        }
        boolean isActiveCoach = coachingRepository.existsByCoach_IdAndAthlete_IdAndStatus(
                currentUserId, requestedAthleteId, CoachingStatus.ACTIVE);
        if (!isActiveCoach) {
            throw new ForbiddenException("No tienes acceso a los datos del atleta " + requestedAthleteId);
        }
        return requestedAthleteId;
    }
}

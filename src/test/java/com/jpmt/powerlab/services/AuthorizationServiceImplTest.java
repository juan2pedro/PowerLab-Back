package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ForbiddenException;
import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.repositories.CoachingRelationshipRepository;
import com.jpmt.powerlab.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private CoachingRelationshipRepository coachingRepository;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Test
    void nullAthlete_resolvesToCurrentUser() {
        when(currentUserProvider.currentUserId()).thenReturn(7L);
        assertEquals(7L, authorizationService.resolveReadableAthlete(null));
    }

    @Test
    void ownAthleteId_resolvesToSelf() {
        when(currentUserProvider.currentUserId()).thenReturn(7L);
        assertEquals(7L, authorizationService.resolveReadableAthlete(7L));
    }

    @Test
    void activeCoach_canReadAthlete() {
        when(currentUserProvider.currentUserId()).thenReturn(1L);
        when(coachingRepository.existsByCoach_IdAndAthlete_IdAndStatus(1L, 9L, CoachingStatus.ACTIVE))
                .thenReturn(true);
        assertEquals(9L, authorizationService.resolveReadableAthlete(9L));
    }

    @Test
    void nonCoach_isForbidden() {
        when(currentUserProvider.currentUserId()).thenReturn(1L);
        when(coachingRepository.existsByCoach_IdAndAthlete_IdAndStatus(1L, 9L, CoachingStatus.ACTIVE))
                .thenReturn(false);
        assertThrows(ForbiddenException.class, () -> authorizationService.resolveReadableAthlete(9L));
    }
}

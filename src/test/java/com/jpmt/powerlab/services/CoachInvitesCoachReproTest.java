package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.models.dto.coaching.CoachingInviteRequest;
import com.jpmt.powerlab.models.dto.coaching.CoachingRelationshipResponse;
import com.jpmt.powerlab.repositories.CoachingRelationshipRepository;
import com.jpmt.powerlab.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CoachInvitesCoachReproTest {

    @Autowired CoachingService coachingService;
    @Autowired UserRepository userRepository;
    @Autowired CoachingRelationshipRepository coachingRepository;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
        coachingRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User saveUser(String email, String username, boolean coach) {
        return userRepository.save(User.builder()
                .email(email).username(username)
                .passwordHash("x").coach(coach).build());
    }

    private void authenticateAs(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, java.util.List.of()));
    }

    @Test
    void coachInvitesAnotherCoach() {
        User coachA = saveUser("a@test.com", "coachA", true);
        User coachB = saveUser("b@test.com", "coachB", true);

        authenticateAs(coachA.getId());
        CoachingRelationshipResponse resp = coachingService.invite(new CoachingInviteRequest("b@test.com"));

        assertNotNull(resp.id());
        assertEquals(CoachingStatus.PENDING, resp.status());
        assertEquals(1, coachingRepository.findByAthlete_Id(coachB.getId()).size(),
                "la invitacion debe persistir con el coach B como atleta");
    }
}

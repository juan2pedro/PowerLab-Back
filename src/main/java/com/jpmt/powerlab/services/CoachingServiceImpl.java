package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.exceptions.ForbiddenException;
import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.CoachingInitiator;
import com.jpmt.powerlab.models.domain.CoachingRelationship;
import com.jpmt.powerlab.models.domain.CoachingStatus;
import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.models.dto.coaching.CoachingInviteRequest;
import com.jpmt.powerlab.models.dto.coaching.CoachingRelationshipResponse;
import com.jpmt.powerlab.repositories.CoachingRelationshipRepository;
import com.jpmt.powerlab.repositories.UserRepository;
import com.jpmt.powerlab.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class CoachingServiceImpl implements CoachingService {

    private final CoachingRelationshipRepository repository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public CoachingServiceImpl(CoachingRelationshipRepository repository, UserRepository userRepository,
                               CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public CoachingRelationshipResponse invite(CoachingInviteRequest request) {
        User coach = currentUserProvider.currentUser();
        User athlete = userRepository.findByEmail(request.athleteEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.athleteEmail()));

        if (athlete.getId().equals(coach.getId())) {
            throw new BadRequestException("No puedes invitarte a ti mismo como atleta.");
        }
        if (repository.existsByCoach_IdAndAthlete_IdAndStatus(coach.getId(), athlete.getId(), CoachingStatus.ACTIVE)) {
            throw new BadRequestException("Ya tienes una relacion activa con este atleta.");
        }
        if (repository.existsByCoach_IdAndAthlete_IdAndStatus(coach.getId(), athlete.getId(), CoachingStatus.PENDING)) {
            throw new BadRequestException("Ya hay una invitacion pendiente para este atleta.");
        }

        CoachingRelationship relationship = CoachingRelationship.builder()
                .coach(coach)
                .athlete(athlete)
                .status(CoachingStatus.PENDING)
                .initiatedBy(CoachingInitiator.COACH)
                .build();
        return toResponse(repository.save(relationship));
    }

    @Override
    public CoachingRelationshipResponse accept(Long id) {
        Long athleteId = currentUserProvider.currentUserId();
        CoachingRelationship relationship = repository.findByIdAndAthlete_Id(id, athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachingRelationship", "id", id));
        if (relationship.getStatus() != CoachingStatus.PENDING) {
            throw new BadRequestException("La invitacion no esta pendiente.");
        }
        if (repository.existsByAthlete_IdAndStatus(athleteId, CoachingStatus.ACTIVE)) {
            throw new BadRequestException("Ya tienes un coach activo; revoca el actual antes de aceptar otro.");
        }
        relationship.setStatus(CoachingStatus.ACTIVE);
        relationship.setRespondedAt(OffsetDateTime.now());
        return toResponse(repository.save(relationship));
    }

    @Override
    public CoachingRelationshipResponse decline(Long id) {
        Long athleteId = currentUserProvider.currentUserId();
        CoachingRelationship relationship = repository.findByIdAndAthlete_Id(id, athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachingRelationship", "id", id));
        if (relationship.getStatus() != CoachingStatus.PENDING) {
            throw new BadRequestException("La invitacion no esta pendiente.");
        }
        relationship.setStatus(CoachingStatus.DECLINED);
        relationship.setRespondedAt(OffsetDateTime.now());
        return toResponse(repository.save(relationship));
    }

    @Override
    public CoachingRelationshipResponse revoke(Long id) {
        Long userId = currentUserProvider.currentUserId();
        CoachingRelationship relationship = repository.findById(id)
                .filter(r -> r.getCoach().getId().equals(userId) || r.getAthlete().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("CoachingRelationship", "id", id));
        if (relationship.getStatus() != CoachingStatus.ACTIVE) {
            throw new BadRequestException("Solo se puede revocar una relacion activa.");
        }
        relationship.setStatus(CoachingStatus.REVOKED);
        relationship.setRespondedAt(OffsetDateTime.now());
        return toResponse(repository.save(relationship));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachingRelationshipResponse> invitations(boolean sent, CoachingStatus status) {
        Long userId = currentUserProvider.currentUserId();
        List<CoachingRelationship> base = sent
                ? repository.findByCoach_Id(userId)
                : repository.findByAthlete_Id(userId);
        return base.stream()
                .filter(r -> status == null || r.getStatus() == status)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachingRelationshipResponse> myAthletes() {
        return repository.findByCoach_IdAndStatus(currentUserProvider.currentUserId(), CoachingStatus.ACTIVE)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoachingRelationshipResponse myCoach() {
        return repository.findByAthlete_IdAndStatus(currentUserProvider.currentUserId(), CoachingStatus.ACTIVE)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No tienes coach activo."));
    }

    private CoachingRelationshipResponse toResponse(CoachingRelationship r) {
        return new CoachingRelationshipResponse(
                r.getId(),
                r.getCoach().getId(),
                r.getCoach().getUsername(),
                r.getAthlete().getId(),
                r.getAthlete().getUsername(),
                r.getStatus(),
                r.getInitiatedBy(),
                r.getCreatedAt(),
                r.getRespondedAt()
        );
    }
}

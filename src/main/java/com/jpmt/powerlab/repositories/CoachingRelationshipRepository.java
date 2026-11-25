package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.CoachingRelationship;
import com.jpmt.powerlab.models.domain.CoachingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoachingRelationshipRepository extends JpaRepository<CoachingRelationship, Long> {

    boolean existsByCoach_IdAndAthlete_IdAndStatus(Long coachId, Long athleteId, CoachingStatus status);

    boolean existsByAthlete_IdAndStatus(Long athleteId, CoachingStatus status);

    List<CoachingRelationship> findByCoach_IdAndStatus(Long coachId, CoachingStatus status);

    Optional<CoachingRelationship> findByAthlete_IdAndStatus(Long athleteId, CoachingStatus status);

    List<CoachingRelationship> findByAthlete_Id(Long athleteId);

    List<CoachingRelationship> findByCoach_Id(Long coachId);

    Optional<CoachingRelationship> findByIdAndAthlete_Id(Long id, Long athleteId);

    Optional<CoachingRelationship> findByIdAndCoach_Id(Long id, Long coachId);
}

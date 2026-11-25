package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.WorkoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {

    // WorkoutEntry no tiene dueno propio: se deriva de su sesion (session.owner).
    Optional<WorkoutEntry> findByIdAndSession_Owner_Id(Long id, Long ownerId);

    boolean existsByIdAndSession_Owner_Id(Long id, Long ownerId);

}

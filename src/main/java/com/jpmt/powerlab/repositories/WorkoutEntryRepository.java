package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.WorkoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {

}

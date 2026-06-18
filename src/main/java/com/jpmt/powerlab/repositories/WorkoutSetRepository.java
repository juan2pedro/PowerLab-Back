package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.WorkoutSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;


public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
    List<WorkoutSet> findByWorkoutEntryId(Long id);
    List<WorkoutSet> findByWorkoutEntryIdIn(Collection<Long> entryIds);

    @Query("""
    select ws from WorkoutSet ws
    where ws.workoutEntry.exercise.id = :exerciseId
    order by ws.workoutEntry.session.date desc, ws.weight desc
""")
    List<WorkoutSet> findLatestForExercise(@Param("exerciseId") Long exerciseId, Pageable pageable);
}

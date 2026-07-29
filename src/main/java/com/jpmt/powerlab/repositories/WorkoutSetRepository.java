package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.domain.WorkoutSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("""
    SELECT ws FROM WorkoutSet ws
    JOIN FETCH ws.workoutEntry we
    JOIN FETCH we.exercise e
    JOIN FETCH we.session s
    WHERE s.date BETWEEN :from AND :to
      AND (:exerciseIds IS NULL OR e.id IN :exerciseIds)
      AND (:patterns IS NULL OR e.mainPattern IN :patterns)
      AND (:workingOnly = false OR we.isWarmup = false)
    ORDER BY s.date, we.displayOrder, ws.sequenceNumber
    """)
    List<WorkoutSet> findForMetrics(LocalDate from, LocalDate to,
                                    List<Long> exerciseIds,
                                    List<MainPattern> patterns,
                                    boolean workingOnly);
}

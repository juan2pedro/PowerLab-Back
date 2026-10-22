package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.MainPattern;
import com.jpmt.powerlab.models.domain.SetType;
import com.jpmt.powerlab.models.domain.WorkoutSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
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

    /**
     * Despacha a una de las 3 queries de abajo según los filtros presentes. Evita el patrón
     * {@code :coleccion IS NULL OR x IN :coleccion}, frágil en Hibernate 6 cuando la colección es null.
     */
    default List<WorkoutSet> findForMetrics(LocalDate from, LocalDate to,
                                             List<Long> exerciseIds,
                                             List<MainPattern> patterns,
                                             Collection<SetType> setTypes) {
        Collection<SetType> types = (setTypes == null || setTypes.isEmpty())
                ? EnumSet.allOf(SetType.class)
                : setTypes;
        boolean byExercise = exerciseIds != null && !exerciseIds.isEmpty();
        boolean byPattern = patterns != null && !patterns.isEmpty();
        if (byExercise) {
            return findForMetricsByExercises(from, to, exerciseIds, types);
        }
        if (byPattern) {
            return findForMetricsByPatterns(from, to, patterns, types);
        }
        return findForMetricsAll(from, to, types);
    }

    @Query("""
    SELECT ws FROM WorkoutSet ws
    JOIN FETCH ws.workoutEntry we
    JOIN FETCH we.exercise e
    JOIN FETCH we.session s
    LEFT JOIN FETCH s.trainingSessionTemplate t
    WHERE s.date BETWEEN :from AND :to
      AND e.id IN :exerciseIds
      AND ws.type IN :setTypes
    ORDER BY s.date, we.displayOrder, ws.sequenceNumber
    """)
    List<WorkoutSet> findForMetricsByExercises(@Param("from") LocalDate from,
                                                @Param("to") LocalDate to,
                                                @Param("exerciseIds") List<Long> exerciseIds,
                                                @Param("setTypes") Collection<SetType> setTypes);

    @Query("""
    SELECT ws FROM WorkoutSet ws
    JOIN FETCH ws.workoutEntry we
    JOIN FETCH we.exercise e
    JOIN FETCH we.session s
    LEFT JOIN FETCH s.trainingSessionTemplate t
    WHERE s.date BETWEEN :from AND :to
      AND e.mainPattern IN :patterns
      AND ws.type IN :setTypes
    ORDER BY s.date, we.displayOrder, ws.sequenceNumber
    """)
    List<WorkoutSet> findForMetricsByPatterns(@Param("from") LocalDate from,
                                               @Param("to") LocalDate to,
                                               @Param("patterns") List<MainPattern> patterns,
                                               @Param("setTypes") Collection<SetType> setTypes);

    @Query("""
    SELECT ws FROM WorkoutSet ws
    JOIN FETCH ws.workoutEntry we
    JOIN FETCH we.exercise e
    JOIN FETCH we.session s
    LEFT JOIN FETCH s.trainingSessionTemplate t
    WHERE s.date BETWEEN :from AND :to
      AND ws.type IN :setTypes
    ORDER BY s.date, we.displayOrder, ws.sequenceNumber
    """)
    List<WorkoutSet> findForMetricsAll(@Param("from") LocalDate from,
                                        @Param("to") LocalDate to,
                                        @Param("setTypes") Collection<SetType> setTypes);
}

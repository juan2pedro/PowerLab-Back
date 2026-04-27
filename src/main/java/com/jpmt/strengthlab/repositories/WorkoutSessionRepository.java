package com.jpmt.strengthlab.repositories;

import com.jpmt.strengthlab.models.domain.WorkoutSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {


    @EntityGraph(attributePaths = {
            "trainingSessionTemplate",
            "entries",
            "entries.exercise"
    })
    Optional<WorkoutSession> findHeaderById(Long id);

    @EntityGraph(attributePaths = {
            "trainingSessionTemplate",
            "entries",
            "entries.exercise"
    })
    Optional<WorkoutSession> findHeaderByDate(LocalDate date);


    @Query("SELECT ws FROM WorkoutSession ws " +
            "JOIN FETCH ws.entries e " +
            "JOIN FETCH e.sets " +
            "WHERE ws.id = :id")
    Optional<WorkoutSession> findFullById(@Param("id") Long id);

    @Query("SELECT ws FROM WorkoutSession ws " +
            "JOIN FETCH ws.entries e " +
            "JOIN FETCH e.sets " +
            "WHERE ws.date = :date")
    Optional<WorkoutSession> findFullByDate(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT ws FROM WorkoutSession ws " +
            "JOIN FETCH ws.entries e " +
            "LEFT JOIN FETCH e.sets " +
            "WHERE ws.date BETWEEN :startDate AND :endDate")
    List<WorkoutSession> findFullByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

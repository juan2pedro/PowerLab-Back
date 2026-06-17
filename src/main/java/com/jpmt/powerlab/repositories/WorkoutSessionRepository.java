package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.WorkoutSession;
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

    @Query("SELECT DISTINCT ws FROM WorkoutSession ws " +
            "LEFT JOIN FETCH ws.entries e " +
            "LEFT JOIN FETCH e.exercise " +
            "WHERE ws.date = :date " +
            "ORDER BY ws.id DESC")
    List<WorkoutSession> findFullByDate(@Param("date") LocalDate date);

    boolean existsByDate(LocalDate date);

    @Query("SELECT DISTINCT ws FROM WorkoutSession ws " +
            "LEFT JOIN FETCH ws.entries e " +
            "WHERE ws.date BETWEEN :startDate AND :endDate")
    List<WorkoutSession> findFullByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

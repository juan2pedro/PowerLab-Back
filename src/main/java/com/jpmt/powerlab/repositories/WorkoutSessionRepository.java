package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.Status;
import com.jpmt.powerlab.models.domain.WorkoutSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {


    @EntityGraph(attributePaths = {
            "trainingSessionTemplate",
            "entries",
            "entries.exercise"
    })
    Optional<WorkoutSession> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT DISTINCT ws FROM WorkoutSession ws " +
            "LEFT JOIN FETCH ws.entries e " +
            "LEFT JOIN FETCH e.exercise " +
            "WHERE ws.date = :date " +
            "AND ws.owner.id = :ownerId " +
            "ORDER BY ws.id DESC")
    List<WorkoutSession> findFullByDate(@Param("date") LocalDate date, @Param("ownerId") Long ownerId);

    boolean existsByDateAndOwner_Id(LocalDate date, Long ownerId);

    boolean existsByIdAndOwner_Id(Long id, Long ownerId);

    @Query("SELECT DISTINCT ws FROM WorkoutSession ws " +
            "LEFT JOIN FETCH ws.entries e " +
            "WHERE ws.owner.id = :ownerId " +
            "AND ws.date BETWEEN :startDate AND :endDate")
    List<WorkoutSession> findFullByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("ownerId") Long ownerId);

    @Query("SELECT s.date FROM WorkoutSession s WHERE s.status IN :statuses AND s.owner.id = :ownerId ORDER BY s.date")
    List<LocalDate> findDatesByStatusIn(@Param("statuses") Collection<Status> statuses,
                                        @Param("ownerId") Long ownerId);

    List<WorkoutSession> findByDateBetweenAndTrainingSessionTemplateIsNotNullAndOwnerId(LocalDate from, LocalDate to, Long ownerId);
}

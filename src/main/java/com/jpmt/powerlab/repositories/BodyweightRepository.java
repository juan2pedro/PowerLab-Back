package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.Bodyweight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BodyweightRepository extends JpaRepository<Bodyweight, Long> {

    /** Carry-forward: el ultimo pesaje con date <= la fecha dada, para ese usuario. */
    Optional<Bodyweight> findTopByUser_IdAndDateLessThanEqualOrderByDateDesc(Long userId, LocalDate date);

    List<Bodyweight> findByUser_IdOrderByDateDesc(Long userId);

    Optional<Bodyweight> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndDate(Long userId, LocalDate date);
}

package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.Exercise;
import com.jpmt.powerlab.models.domain.MainPattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByMainPattern(MainPattern mainPattern);
}

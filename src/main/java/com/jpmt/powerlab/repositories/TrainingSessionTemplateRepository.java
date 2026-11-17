package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.TrainingSessionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionTemplateRepository extends JpaRepository<TrainingSessionTemplate, Long> {

    @Override
    @Query("SELECT t FROM TrainingSessionTemplate t JOIN FETCH t.block")
    List<TrainingSessionTemplate> findAll();

    List<TrainingSessionTemplate> findByBlock_IdAndWeekNumberOrderByDayIndexAsc(Long blockId, Integer weekNumber);

    @Query("SELECT t FROM TrainingSessionTemplate t " +
            "LEFT JOIN FETCH t.trainingSets st " +
            "LEFT JOIN FETCH st.exercise " +
            "WHERE t.id = :id " +
            "ORDER BY st.displayOrder ASC")
    Optional<TrainingSessionTemplate> findByIdOrderByDisplayOrder(@Param("id") Long id);

}

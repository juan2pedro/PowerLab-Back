package com.jpmt.strengthlab.repositories;

import com.jpmt.strengthlab.models.domain.TrainingSessionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionTemplateRepository extends JpaRepository<TrainingSessionTemplate, Long> {
    List<TrainingSessionTemplate> findByBlockNameAndWeekNumberOrderByDayInWeekAsc(String blockName, Integer weekNumber);

    @Query("SELECT t FROM TrainingSessionTemplate t " +
            "LEFT JOIN FETCH t.setTemplates st " +
            "LEFT JOIN FETCH st.exercise " +
            "WHERE t.id = :id")
    Optional<TrainingSessionTemplate> findByIdOrderByDisplayOrder(@Param("id") Long id);

}

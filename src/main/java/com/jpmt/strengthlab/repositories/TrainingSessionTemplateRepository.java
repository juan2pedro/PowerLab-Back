package com.jpmt.strengthlab.repositories;

import com.jpmt.strengthlab.models.domain.TrainingSessionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingSessionTemplateRepository extends JpaRepository<TrainingSessionTemplate, Long> {
    List<TrainingSessionTemplate> findByBlockNameAndWeekNumberOrderByDayInWeekAsc(String blockName, Integer weekNumber);
}

package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.TrainingSetTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSetTemplateRepository extends JpaRepository<TrainingSetTemplate, Long> {

}

package com.jpmt.strengthlab.repositories;

import com.jpmt.strengthlab.models.domain.TrainingSetTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSetTemplateRepository extends JpaRepository<TrainingSetTemplate, Long> {

}

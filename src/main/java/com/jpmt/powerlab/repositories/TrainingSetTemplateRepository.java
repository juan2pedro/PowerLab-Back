package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.TrainingSetTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TrainingSetTemplateRepository extends JpaRepository<TrainingSetTemplate, Long> {

    @Query("""
    SELECT tst FROM TrainingSetTemplate tst
    JOIN FETCH tst.exercise
    WHERE tst.sessionTemplate.id IN :sessionTemplateIds
    """)
    List<TrainingSetTemplate> findBySessionTemplateIdIn(
            @Param("sessionTemplateIds") Collection<Long> sessionTemplateIds);

    List<TrainingSetTemplate> findBySessionTemplateId(Long sessionId);
}

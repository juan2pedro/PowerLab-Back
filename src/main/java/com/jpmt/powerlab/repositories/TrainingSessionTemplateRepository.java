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

    @Query("SELECT t FROM TrainingSessionTemplate t JOIN FETCH t.block b WHERE b.owner.id = :ownerId")
    List<TrainingSessionTemplate> findAllByOwnerId(@Param("ownerId") Long ownerId);

    List<TrainingSessionTemplate> findByBlock_IdAndWeekNumberOrderByDayIndexAsc(Long blockId, Integer weekNumber);

    List<TrainingSessionTemplate> findByBlock_IdAndWeekNumberAndBlock_Owner_IdOrderByDayIndexAsc(
            Long blockId, Integer weekNumber, Long ownerId);

    @Query("SELECT t FROM TrainingSessionTemplate t " +
            "LEFT JOIN FETCH t.trainingSets st " +
            "LEFT JOIN FETCH st.exercise " +
            "WHERE t.id = :id " +
            "ORDER BY st.displayOrder ASC")
    Optional<TrainingSessionTemplate> findByIdOrderByDisplayOrder(@Param("id") Long id);

    @Query("SELECT t FROM TrainingSessionTemplate t " +
            "LEFT JOIN FETCH t.trainingSets st " +
            "LEFT JOIN FETCH st.exercise " +
            "WHERE t.id = :id AND t.block.owner.id = :ownerId " +
            "ORDER BY st.displayOrder ASC")
    Optional<TrainingSessionTemplate> findByIdAndOwnerOrderByDisplayOrder(@Param("id") Long id,
                                                                         @Param("ownerId") Long ownerId);

    boolean existsByIdAndBlock_Owner_Id(Long id, Long ownerId);

}

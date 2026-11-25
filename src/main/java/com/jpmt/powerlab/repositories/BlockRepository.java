package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository  extends JpaRepository<Block, Long> {

    List<Block> findByOwner_Id(Long ownerId);

    Optional<Block> findByIdAndOwner_Id(Long id, Long ownerId);

    boolean existsByIdAndOwner_Id(Long id, Long ownerId);
}

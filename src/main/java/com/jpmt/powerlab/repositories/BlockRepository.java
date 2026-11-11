package com.jpmt.powerlab.repositories;

import com.jpmt.powerlab.models.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository  extends JpaRepository<Block, Long> {
}

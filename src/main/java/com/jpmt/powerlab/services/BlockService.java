package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Block;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BlockService {

    List<Block> findAll();

    Block findById(Long id);

    Block save(Block block);

    Block update(Long id, Block block);
}

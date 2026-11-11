package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Block;

import java.util.List;

public interface BlockService {

    List<Block> findAll();

    Block findById(Long id);

    Block save(Block block);

    Block update(Long id, Block block);
}

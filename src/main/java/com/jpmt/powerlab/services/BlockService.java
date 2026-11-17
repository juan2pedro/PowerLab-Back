package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.dto.block.BlockRequest;
import com.jpmt.powerlab.models.dto.block.BlockResponse;

import java.util.List;


public interface BlockService {

    List<BlockResponse> findAll();

    BlockResponse findById(Long id);

    BlockResponse save(BlockRequest block);

    BlockResponse update(Long id, BlockRequest block);

    void deleteById(Long id);
}

package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.Block;
import com.jpmt.powerlab.models.dto.block.BlockRequest;
import com.jpmt.powerlab.models.dto.block.BlockResponse;
import com.jpmt.powerlab.models.mappers.BlockMapper;
import com.jpmt.powerlab.repositories.BlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlockServiceImpl implements BlockService{

    private final BlockRepository repository;
    private final BlockMapper blockMapper;

    public BlockServiceImpl(BlockRepository repository, BlockMapper blockMapper) {
        this.repository = repository;
        this.blockMapper = blockMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponse> findAll() {
        List<Block> entities = repository.findAll();
        return entities.stream().map(blockMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BlockResponse findById(Long id) {
        Block entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        return blockMapper.toResponse(entity);

    }

    @Transactional
    @Override
    public BlockResponse save(BlockRequest block) {
        Block entity = blockMapper.toEntity(block);
        Block saved = repository.save(entity);
        return blockMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public BlockResponse update(Long id, BlockRequest block) {
        Block entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        blockMapper.updateEntityFromRequest(block, entity);
        Block updated = repository.save(entity);
        return blockMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Block", "id", id);
        }
        repository.deleteById(id);
    }
}

package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.Block;
import com.jpmt.powerlab.models.dto.block.BlockRequest;
import com.jpmt.powerlab.models.dto.block.BlockResponse;
import com.jpmt.powerlab.models.mappers.BlockMapper;
import com.jpmt.powerlab.repositories.BlockRepository;
import com.jpmt.powerlab.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlockServiceImpl implements BlockService{

    private final BlockRepository repository;
    private final BlockMapper blockMapper;
    private final CurrentUserProvider currentUserProvider;

    public BlockServiceImpl(BlockRepository repository, BlockMapper blockMapper, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.blockMapper = blockMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponse> findAll() {
        List<Block> entities = repository.findByOwner_Id(currentUserProvider.currentUserId());
        return entities.stream().map(blockMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BlockResponse findById(Long id) {
        Block entity = repository.findByIdAndOwner_Id(id, currentUserProvider.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        return blockMapper.toResponse(entity);

    }

    @Transactional
    @Override
    public BlockResponse save(BlockRequest block) {
        Block entity = blockMapper.toEntity(block);
        entity.setOwner(currentUserProvider.currentUser());
        Block saved = repository.save(entity);
        return blockMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public BlockResponse update(Long id, BlockRequest block) {
        Block entity = repository.findByIdAndOwner_Id(id, currentUserProvider.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        blockMapper.updateEntityFromRequest(block, entity);
        Block updated = repository.save(entity);
        return blockMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (!repository.existsByIdAndOwner_Id(id, currentUserProvider.currentUserId())) {
            throw new ResourceNotFoundException("Block", "id", id);
        }
        repository.deleteById(id);
    }
}

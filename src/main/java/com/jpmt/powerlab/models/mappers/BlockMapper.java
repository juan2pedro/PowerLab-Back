package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.Block;
import com.jpmt.powerlab.models.dto.block.BlockRequest;
import com.jpmt.powerlab.models.dto.block.BlockResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.ERROR)
public interface BlockMapper {
    BlockResponse toResponse(Block entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Block toEntity(BlockRequest dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromRequest(
            BlockRequest request,
           @MappingTarget Block entity
    );


}

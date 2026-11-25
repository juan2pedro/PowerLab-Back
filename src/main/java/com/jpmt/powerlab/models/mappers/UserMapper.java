package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.models.dto.user.RegisterRequest;
import com.jpmt.powerlab.models.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.ERROR)
public interface UserMapper {


    UserResponse toResponse(User entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coach", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "defaultPrVisibility", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(RegisterRequest dto);
}

package com.qburst.microservice.auth.mapper;

import com.qburst.microservice.auth.dto.request.user.UserRequest;
import com.qburst.microservice.auth.dto.response.user.UserResponse;
import com.qburst.microservice.auth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map Request Record -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserRequest request);

    // Map Entity -> Response Record
    UserResponse toResponse(UserEntity entity);
}
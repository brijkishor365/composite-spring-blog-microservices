package com.qburst.microservice.post.mapper;

import com.qburst.microservice.post.dto.request.category.CategoryRequest;
import com.qburst.microservice.post.dto.response.category.CategoryResponse;
import com.qburst.microservice.post.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = false)
    @Mapping(target = "description", ignore = false)
    CategoryEntity toEntity(CategoryRequest category);

    CategoryResponse toResponse(CategoryEntity categoryEntity);

    List<CategoryResponse> toResponseList(List<CategoryEntity> entities);

    @Mapping(target = "id", ignore = true) // Ensure the ID isn't overwritten by the DTO
    void updateEntityFromDto(CategoryRequest category, @MappingTarget CategoryEntity entity);
}

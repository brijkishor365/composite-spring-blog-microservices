package com.qburst.microservice.post.mapper;

import com.qburst.microservice.post.dto.request.post.PostRequest;
import com.qburst.microservice.post.dto.response.post.PostResponse;
import com.qburst.microservice.post.entity.PostEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    // Convert Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "slug", ignore = true)
    PostEntity toEntity(PostRequest request);

    // Convert Entity to Response
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "authorId", target = "authorFullName")
    PostResponse toResponse(PostEntity blogEntity);

    List<PostResponse> toResponseList(List<PostEntity> entities);

    // Update existing Entity from DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    void updateEntityFromDto(PostRequest request, @MappingTarget PostEntity entity);
}
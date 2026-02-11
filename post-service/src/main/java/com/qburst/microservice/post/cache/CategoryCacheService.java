package com.qburst.microservice.post.cache;

import com.qburst.microservice.post.dto.response.category.CategoryResponse;
import com.qburst.microservice.post.entity.CategoryEntity;
import com.qburst.microservice.post.exception.category.CategoryNotFoundException;
import com.qburst.microservice.post.mapper.CategoryMapper;
import com.qburst.microservice.post.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryCacheService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Cacheable(value = "categories", key = "#categoryId", unless = "#result == null")
    public CategoryResponse getCategoryById(Long categoryId) {

        log.info("Cache MISS -> Fetching categoryId={} from DB", categoryId);

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist")
                );

        return categoryMapper.toResponse(category);
    }
}

package com.qburst.microservice.post.service.category.Impl;

import com.qburst.microservice.post.cache.CategoryCacheService;
import com.qburst.microservice.post.dto.request.category.CategoryRequest;
import com.qburst.microservice.post.dto.response.category.CategoryResponse;
import com.qburst.microservice.post.entity.CategoryEntity;
import com.qburst.microservice.post.exception.category.CategoryNameAlreadyExistSException;
import com.qburst.microservice.post.exception.category.CategoryNotFoundException;
import com.qburst.microservice.post.mapper.CategoryMapper;
import com.qburst.microservice.post.repository.CategoryRepository;
import com.qburst.microservice.post.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryCacheService categoryCacheService;

    @Override
    @Transactional
    @CachePut(value = "categories", key = "#result.id")
    public CategoryResponse createCategory(CategoryRequest category) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findCategoryByName(category.name());

        if (!categoryEntityList.isEmpty()) {
            throw new CategoryNameAlreadyExistSException("Category '" + category.name() + "' is already taken");
        }

        CategoryEntity categoryEntity = categoryMapper.toEntity(category);

        CategoryEntity newCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toResponse(newCategory);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"categories"},
            key = "#categoryId"
    )
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest category) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist"));

        // Only throw if the name is taken by a DIFFERENT ID
        categoryRepository.findCategoryByName(category.name())
                .stream()
                .filter(c -> !c.getId().equals(categoryId)) // Check for ID mismatch
                .findAny()
                .ifPresent(c -> {
                    throw new CategoryNameAlreadyExistSException("Category '" + category.name() + "' is already taken");
                });

        // Update the managed entity using the mapper
        categoryMapper.updateEntityFromDto(category, existingCategory);

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist"));

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        return categoryCacheService.getCategoryById(categoryId);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<CategoryEntity> categories = categoryRepository.findAll(pageable);

        return categories.map(categoryMapper::toResponse);
    }
}

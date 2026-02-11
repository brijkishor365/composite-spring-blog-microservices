package com.qburst.microservice.post.service.category;

import com.qburst.microservice.post.dto.request.category.CategoryRequest;
import com.qburst.microservice.post.dto.response.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest category);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest category);

    void deleteCategory(Long categoryId);

    CategoryResponse getCategoryById(Long categoryId);

    Page<CategoryResponse> getAllCategories(Pageable pageable);
}

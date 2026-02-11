package com.qburst.microservice.post.controller;

import com.qburst.microservice.post.dto.request.category.CategoryRequest;
import com.qburst.microservice.post.dto.response.category.CategoryResponse;
import com.qburst.microservice.post.service.category.Impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getCategories(@PageableDefault(size = 5, sort = "id") Pageable pageable) {
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest category) {
        CategoryResponse newCategory = categoryService.createCategory(category);

        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody CategoryRequest category) {
        CategoryResponse category1 = categoryService.updateCategory(categoryId, category);

        return new ResponseEntity<>(category1, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        log.info("Deleting category: {}", categoryId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(category);
    }

    // TODO:: Need to implement
//    GET /categories/{categoryId}/posts

}

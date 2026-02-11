package com.qburst.microservice.post.dto.response.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryResponse(
        Long id,

        @NotBlank(message = "Category name is required")
        String name,
        String description
) {
}

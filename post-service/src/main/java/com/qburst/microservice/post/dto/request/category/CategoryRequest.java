package com.qburst.microservice.post.dto.request.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        Long id,

        @NotBlank(message = "Category name is required")
        String name,
        String description
) {
}

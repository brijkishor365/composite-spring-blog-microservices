package com.qburst.microservice.post.exception.category;

import com.qburst.microservice.post.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApplicationException {
    public CategoryNotFoundException(String message) {
        super(message, "Category not found", HttpStatus.NOT_FOUND);
    }
}

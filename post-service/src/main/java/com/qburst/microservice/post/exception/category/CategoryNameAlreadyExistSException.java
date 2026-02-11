package com.qburst.microservice.post.exception.category;

import com.qburst.microservice.post.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryNameAlreadyExistSException extends ApplicationException {
    public CategoryNameAlreadyExistSException(String message) {
        super(message, "Category Conflict", HttpStatus.CONFLICT);
    }
}

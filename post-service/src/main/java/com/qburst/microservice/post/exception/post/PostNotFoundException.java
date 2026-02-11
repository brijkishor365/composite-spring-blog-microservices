package com.qburst.microservice.post.exception.post;

import com.qburst.microservice.post.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApplicationException {
    public PostNotFoundException(String message) {
        super(message, "Post not found", HttpStatus.NOT_FOUND);
    }
}

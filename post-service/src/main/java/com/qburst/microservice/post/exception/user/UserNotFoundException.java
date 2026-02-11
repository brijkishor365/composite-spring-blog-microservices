package com.qburst.microservice.post.exception.user;

import com.qburst.microservice.post.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(String message) {
        super(message, "User Not Found", HttpStatus.NOT_FOUND);
    }
}

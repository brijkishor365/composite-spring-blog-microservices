package com.qburst.microservice.auth.exception.user;

import com.qburst.microservice.auth.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNameAlreadyExistsException extends ApplicationException {
    public UserNameAlreadyExistsException(String message) {
        super(message, "Username Conflict", HttpStatus.CONFLICT);
    }
}

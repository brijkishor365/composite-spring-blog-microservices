package com.qburst.microservice.auth.exception.auth;

import com.qburst.microservice.auth.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class InvalidUserNameOrPasswordException extends ApplicationException {
    public InvalidUserNameOrPasswordException(String message) {
        super(message, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}

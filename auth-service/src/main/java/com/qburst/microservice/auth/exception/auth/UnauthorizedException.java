package com.qburst.microservice.auth.exception.auth;

import com.qburst.microservice.auth.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(message, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}

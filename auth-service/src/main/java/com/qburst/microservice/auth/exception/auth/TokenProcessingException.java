package com.qburst.microservice.auth.exception.auth;

import com.qburst.microservice.auth.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class TokenProcessingException extends ApplicationException {
    public TokenProcessingException(String message) {
        super(message, "Token Error", HttpStatus.UNAUTHORIZED);
    }
}

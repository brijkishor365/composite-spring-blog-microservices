package com.qburst.microservice.auth.exception.auth;

public class TokenMissingException extends RuntimeException {
    public TokenMissingException(String message) {
        super(message);
    }
}

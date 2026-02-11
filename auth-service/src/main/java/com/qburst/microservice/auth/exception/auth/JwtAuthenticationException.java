package com.qburst.microservice.auth.exception.auth;

import com.qburst.microservice.auth.exception.base.JwtErrorType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private final JwtErrorType errorType;

    public JwtAuthenticationException(
            JwtErrorType errorType,
            String message,
            Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

}

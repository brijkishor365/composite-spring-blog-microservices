package com.qburst.microservice.auth.security;

import com.qburst.microservice.auth.exception.ApiErrorResponse;
import com.qburst.microservice.auth.exception.auth.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
//import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        String message = getMessage(authException);

        log.warn("Authentication failed: {} [{}]",
                message, request.getRequestURI());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message(message)
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }

    private static String getMessage(AuthenticationException authException) {
        String message = "Unauthorized";

        if (authException instanceof JwtAuthenticationException jwtEx) {
            message = switch (jwtEx.getErrorType()) {
                case EXPIRED -> "JWT expired";
                case MALFORMED -> "Invalid token format";
                case UNSUPPORTED -> "JWT unsupported";
                case SIGNATURE_INVALID -> "JWT signature invalid";
                case EMPTY -> "JWT token missing";
                case BLACKLISTED -> "Token is blacklisted";
            };
        }
        return message;
    }
}

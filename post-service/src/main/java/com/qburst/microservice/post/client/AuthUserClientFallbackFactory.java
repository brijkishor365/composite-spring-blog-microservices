package com.qburst.microservice.post.client;

import com.qburst.microservice.post.dto.common.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class AuthUserClientFallbackFactory
        implements FallbackFactory<AuthUserClient> {

    @Override
    public AuthUserClient create(Throwable cause) {

        return userId -> {
            if (cause instanceof TimeoutException) {
                // Maybe cached user
                // return cachedUser(userId);
            }

            // Auth service DOWN → unsafe
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth service unavailable"
            );
        };
    }

}


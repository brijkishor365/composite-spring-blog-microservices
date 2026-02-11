package com.qburst.microservice.post.client;

import com.qburst.microservice.post.dto.common.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "auth-service",
        path = "/users",
        fallbackFactory = AuthUserClientFallbackFactory.class
)
public interface AuthUserClient {

    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId
//            ,@RequestHeader("X-Request-Id") String requestId
    );
}

package com.qburst.microservice.auth.service.user;

import com.qburst.microservice.auth.dto.request.user.UserRequest;
import com.qburst.microservice.auth.dto.response.user.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserService {

    UserResponse registerUser(UserRequest user) throws Exception;

    void logout(String authToken);

    UserResponse getUserProfile(String username);

    UserResponse getUserById(Long userId);

    void deleteUser(@PathVariable Long userId);
}

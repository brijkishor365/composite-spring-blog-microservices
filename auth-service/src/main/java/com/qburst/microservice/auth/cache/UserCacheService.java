package com.qburst.microservice.auth.cache;

import com.qburst.microservice.auth.dto.response.user.UserResponse;
import com.qburst.microservice.auth.entity.UserEntity;
import com.qburst.microservice.auth.exception.user.UserNotFoundException;
import com.qburst.microservice.auth.mapper.UserMapper;
import com.qburst.microservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(
            value = "users",
            key = "#userId",
            unless = "#result == null"
    )
    public UserResponse getUserById(Long userId) {

        log.info("Cache MISS → Fetching userId={} from auth-service", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with ID: " + userId)
                );

        return userMapper.toResponse(user);
    }
}

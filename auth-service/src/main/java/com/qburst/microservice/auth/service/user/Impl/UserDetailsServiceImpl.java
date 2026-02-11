package com.qburst.microservice.auth.service.user.Impl;

import com.qburst.microservice.auth.entity.UserEntity;
import com.qburst.microservice.auth.repository.UserRepository;
import com.qburst.microservice.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntities = userRepository.findByUsername(username);

        if (userEntities.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new UserPrincipal(userEntities.get());
    }
}

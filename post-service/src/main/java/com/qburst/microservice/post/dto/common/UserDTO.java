package com.qburst.microservice.post.dto.common;

public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String username,
        String email,
        String role
) {
    public String fullName() {
        return firstname + " " + lastname;
    }
}


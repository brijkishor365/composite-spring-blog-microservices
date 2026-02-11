package com.qburst.microservice.auth.dto.request.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRequest(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Hide in swagger
        Long id,

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
        String username,

        @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) // Hide in swagger
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
        String password,

        String roles,

        String firstname,

        String lastname,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email
) {
}
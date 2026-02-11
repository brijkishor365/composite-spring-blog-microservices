package com.qburst.microservice.auth.controller;

import com.qburst.microservice.auth.dto.request.auth.LoginRequest;
import com.qburst.microservice.auth.dto.request.auth.ResetPasswordRequest;
import com.qburst.microservice.auth.dto.request.user.UserRequest;
import com.qburst.microservice.auth.dto.response.auth.AuthResponse;
import com.qburst.microservice.auth.dto.response.user.UserResponse;
import com.qburst.microservice.auth.exception.ApiErrorResponse;
import com.qburst.microservice.auth.service.user.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication, tokens, and session management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
})
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Login with username ans password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization") String authorization) {
        // Controller just passes the raw data and returns the correct status
        userService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("If an account exists, an OTP has been sent.");
    }

    @PostMapping("/password/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok("Password reset successful.");
    }

//     @PostMapping("/refresh")
//     public ResponseEntity<AuthResponse> refreshToken(@RequestBody
//     TokenRefreshRequest request) {
//     return ResponseEntity.ok(authService.refreshToken(request));
//     }
}

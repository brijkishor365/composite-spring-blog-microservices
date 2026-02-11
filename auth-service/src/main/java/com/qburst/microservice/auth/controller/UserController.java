package com.qburst.microservice.auth.controller;

import com.qburst.microservice.auth.dto.request.user.UserRequest;
import com.qburst.microservice.auth.dto.response.user.UserListResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User lifecycle, profile management, and admin operations")
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
@RequiredArgsConstructor // Automatically generates the constructor for all 'final' fields
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @Operation(summary = "Get paginated list of users (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping //    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserListResponse>> getUsers(
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("CURRENT USER AUTHORITIES: {}", auth.getAuthorities());

        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @Operation(summary = "Get user by id (Admin only)")
    @GetMapping(value = "/{userId}", headers = "X-API-VERSION=1")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {

        UserResponse userDetails = userService.getUserById(userId);

        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @Operation(
            summary = "Get current logged-in user profile",
            description = "Returns profile details of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAdminProfile(
            @AuthenticationPrincipal(expression = "username") String username) {

        UserResponse userProfile = userService.getUserProfile(username);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping(value = "/me", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponse> updateAdminProfile(
            @AuthenticationPrincipal(expression = "username") String username,
            @Valid @RequestBody UserRequest request) {

        UserResponse updatedUser = userService.updateProfile(username, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("ADMIN")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Request to delete user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    // TODO:: pending routes
//    /users/{userId}/status
//    /users/{userId}/roles
//    /users/{userId}/preferences
//
//    /users/search
//    /users?page=1&size=20

}

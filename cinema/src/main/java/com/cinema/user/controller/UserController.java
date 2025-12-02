package com.cinema.user.controller;

import com.cinema.shared.dto.ApiResponse;
import com.cinema.user.dto.ChangePasswordRequest;
import com.cinema.user.dto.UpdateProfileRequest;
import com.cinema.user.dto.UserResponse;
import com.cinema.user.entity.User;
import com.cinema.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile management APIs")
public class UserController {

    private final UserService userService;

    public record PublicProfile(String username, String fullName, String avatarUrl, LocalDateTime createdAt) {}

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal User user) {
        UserResponse response = userService.getUserById(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping("/me/points")
    @Operation(summary = "Get current user loyalty points")
    public ResponseEntity<ApiResponse<UserResponse>> getMyPoints(@AuthenticationPrincipal User user) {
        UserResponse response = userService.getUserById(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user public profile by username")
    public ResponseEntity<ApiResponse<PublicProfile>> getUserPublicProfile(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        // Only return public info
        PublicProfile publicProfile = new PublicProfile(
                response.getUsername(),
                response.getFullName(),
                response.getAvatarUrl() != null ? response.getAvatarUrl() : "",
                response.getCreatedAt()
        );
        return ResponseEntity.ok(ApiResponse.success(publicProfile));
    }
}

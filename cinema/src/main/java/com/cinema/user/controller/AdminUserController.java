package com.cinema.user.controller;

import com.cinema.shared.dto.ApiResponse;
import com.cinema.user.dto.AdminUpdateUserRequest;
import com.cinema.user.dto.UserResponse;
import com.cinema.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - User Management", description = "Admin APIs for user management")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by admin")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        UserResponse user = userService.adminUpdateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/{id}/add-points")
    @Operation(summary = "Add loyalty points to user")
    public ResponseEntity<ApiResponse<Void>> addPoints(
            @PathVariable Long id,
            @RequestParam int points) {
        userService.addPoints(id, points);
        return ResponseEntity.ok(ApiResponse.success("Points added successfully"));
    }

    @PostMapping("/{id}/deduct-points")
    @Operation(summary = "Deduct loyalty points from user")
    public ResponseEntity<ApiResponse<Void>> deductPoints(
            @PathVariable Long id,
            @RequestParam int points) {
        userService.deductPoints(id, points);
        return ResponseEntity.ok(ApiResponse.success("Points deducted successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update user status (ACTIVE, INACTIVE, BANNED)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setStatus(status);
        UserResponse user = userService.adminUpdateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role (CUSTOMER, STAFF, ADMIN)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setRole(role);
        UserResponse user = userService.adminUpdateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}

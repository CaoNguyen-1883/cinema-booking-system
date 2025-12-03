package com.cinema.user.service;

import com.cinema.shared.exception.BusinessException;
import com.cinema.user.dto.*;
import com.cinema.user.entity.User;
import com.cinema.user.entity.User.UserRole;
import com.cinema.user.entity.User.UserStatus;
import com.cinema.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .phoneNumber("0123456789")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .points(1000)
                .tokenVersion(0L)
                .build();
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID successfully")
        void getUserById_Success() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            UserResponse response = userService.getUserById(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void getUserById_NotFound_ThrowsException() {
            // Given
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get user by username successfully")
        void getUserByUsername_Success() {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When
            UserResponse response = userService.getUserByUsername("testuser");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should throw exception when user not found by username")
        void getUserByUsername_NotFound_ThrowsException() {
            // Given
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUserByUsername("unknown"))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Update Profile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void updateProfile_Success() {
            // Given
            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setFullName("Updated Name");
            request.setPhoneNumber("0987654321");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            UserResponse response = userService.updateProfile(1L, request);

            // Then
            assertThat(response).isNotNull();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found for profile update")
        void updateProfile_UserNotFound_ThrowsException() {
            // Given
            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setFullName("Updated Name");
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.updateProfile(999L, request))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("oldPassword");
            request.setNewPassword("newPassword123");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.changePassword(1L, request);

            // Then
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for incorrect current password")
        void changePassword_WrongCurrentPassword_ThrowsException() {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("wrongPassword");
            request.setNewPassword("newPassword123");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> userService.changePassword(1L, request))
                    .isInstanceOf(BusinessException.class);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Admin Operations Tests")
    class AdminOperationsTests {

        @Test
        @DisplayName("Should get all users with pagination")
        void getAllUsers_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(testUser));
            when(userRepository.findAll(pageable)).thenReturn(userPage);

            // When
            Page<UserResponse> result = userService.getAllUsers(pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should admin update user successfully")
        void adminUpdateUser_Success() {
            // Given
            AdminUpdateUserRequest request = new AdminUpdateUserRequest();
            request.setFullName("Admin Updated Name");
            request.setRole("ADMIN");
            request.setStatus("ACTIVE");
            request.setPoints(5000);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            UserResponse response = userService.adminUpdateUser(1L, request);

            // Then
            assertThat(response).isNotNull();
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Points Management Tests")
    class PointsManagementTests {

        @Test
        @DisplayName("Should add points successfully")
        void addPoints_Success() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.addPoints(1L, 500);

            // Then
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should deduct points successfully")
        void deductPoints_Success() {
            // Given
            testUser.setPoints(1000);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.deductPoints(1L, 500);

            // Then
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when insufficient points")
        void deductPoints_InsufficientPoints_ThrowsException() {
            // Given
            testUser.setPoints(100);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When & Then
            assertThatThrownBy(() -> userService.deductPoints(1L, 500))
                    .isInstanceOf(BusinessException.class);
            verify(userRepository, never()).save(any(User.class));
        }
    }
}

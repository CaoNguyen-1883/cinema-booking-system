package com.cinema.auth.service;

import com.cinema.auth.config.JwtProperties;
import com.cinema.auth.dto.AuthResponse;
import com.cinema.auth.dto.LoginRequest;
import com.cinema.auth.dto.RegisterRequest;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.service.EmailService;
import com.cinema.user.entity.User;
import com.cinema.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtProperties.Cookie cookieConfig;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletResponse httpResponse;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .role(User.UserRole.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .points(0)
                .tokenVersion(0L)
                .build();

        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .fullName("New User")
                .build();

        loginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully")
        void register_Success() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);

            // When
            AuthResponse response = authService.register(registerRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username exists")
        void register_UsernameExists_ThrowsException() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(BusinessException.class);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void register_EmailExists_ThrowsException() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(BusinessException.class);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @BeforeEach
        void setUpMocks() {
            when(jwtProperties.getCookie()).thenReturn(cookieConfig);
            when(cookieConfig.getName()).thenReturn("refreshToken");
            when(cookieConfig.isSecure()).thenReturn(false);
            when(cookieConfig.getMaxAge()).thenReturn(86400);
        }

        @Test
        @DisplayName("Should login successfully")
        void login_Success() {
            // Given
            when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);

            // When
            AuthResponse response = authService.login(loginRequest, httpResponse);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should throw exception for invalid password")
        void login_InvalidPassword_ThrowsException() {
            // Given
            when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.login(loginRequest, httpResponse))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_UserNotFound_ThrowsException() {
            // Given
            when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> authService.login(loginRequest, httpResponse))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Should throw exception when user is inactive")
        void login_InactiveUser_ThrowsException() {
            // Given
            testUser.setStatus(User.UserStatus.BANNED);
            when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                    .thenReturn(Optional.of(testUser));

            // When & Then
            assertThatThrownBy(() -> authService.login(loginRequest, httpResponse))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {

        @BeforeEach
        void setUpMocks() {
            when(jwtProperties.getCookie()).thenReturn(cookieConfig);
            when(cookieConfig.getName()).thenReturn("refreshToken");
            when(cookieConfig.isSecure()).thenReturn(false);
            when(cookieConfig.getMaxAge()).thenReturn(86400);
        }

        @Test
        @DisplayName("Should refresh token successfully")
        void refreshToken_Success() {
            // Given
            String refreshToken = "validRefreshToken";
            when(jwtService.validateToken(refreshToken)).thenReturn(true);
            when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
            when(jwtService.extractTokenVersion(refreshToken)).thenReturn(0L);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(jwtService.generateAccessToken(any(User.class))).thenReturn("newAccessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("newRefreshToken");
            when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);

            // When
            AuthResponse response = authService.refreshToken(refreshToken, httpResponse);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        }

        @Test
        @DisplayName("Should throw exception for invalid refresh token")
        void refreshToken_InvalidToken_ThrowsException() {
            // Given
            String refreshToken = "invalidRefreshToken";
            when(jwtService.validateToken(refreshToken)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.refreshToken(refreshToken, httpResponse))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Should throw exception for null refresh token")
        void refreshToken_NullToken_ThrowsException() {
            // When & Then
            assertThatThrownBy(() -> authService.refreshToken(null, httpResponse))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }
}

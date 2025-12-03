package com.cinema.auth.service;

import com.cinema.auth.config.JwtProperties;
import com.cinema.auth.dto.AuthResponse;
import com.cinema.auth.dto.LoginRequest;
import com.cinema.auth.dto.RegisterRequest;
import com.cinema.shared.service.EmailService;
import com.cinema.user.entity.User;
import com.cinema.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USER_USERNAME_EXISTS);
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS);
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(User.UserRole.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .points(0)
                .tokenVersion(0L)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
            // Don't fail registration if email fails
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);

        return buildAuthResponse(user, accessToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(
                        request.getUsernameOrEmail(),
                        request.getUsernameOrEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        // Check if user is active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Set refresh token in HttpOnly cookie
        setRefreshTokenCookie(response, refreshToken);

        // Update last login
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in: {}", user.getUsername());

        return buildAuthResponse(user, accessToken);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Refresh token is required");
        }

        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Check if it's a refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid token type");
        }

        // Extract user info
        String username = jwtService.extractUsername(refreshToken);
        Long tokenVersion = jwtService.extractTokenVersion(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Verify token version
        if (!tokenVersion.equals(user.getTokenVersion())) {
            throw new BadCredentialsException("Token has been revoked");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Set new refresh token in cookie
        setRefreshTokenCookie(response, newRefreshToken);

        log.debug("Token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(user, newAccessToken);
    }

    @Transactional
    public void logout(User user, HttpServletResponse response) {
        // Increment token version to invalidate all existing tokens
        user.incrementTokenVersion();
        userRepository.save(user);

        // Clear refresh token cookie
        clearRefreshTokenCookie(response);

        log.info("User logged out: {}", user.getUsername());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(jwtProperties.getCookie().getName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.getCookie().isSecure());
        cookie.setPath("/api/auth");
        cookie.setMaxAge(jwtProperties.getCookie().getMaxAge());
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtProperties.getCookie().getName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtProperties.getCookie().isSecure());
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000) // seconds
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .points(user.getPoints())
                        .build())
                .build();
    }
}

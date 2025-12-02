package com.cinema.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Authentication (1xxx)
    AUTH_INVALID_CREDENTIALS(1001, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED(1002, "Access token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID(1003, "Invalid access token", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1004, "Access denied", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_DISABLED(1005, "User account is disabled", HttpStatus.UNAUTHORIZED),
    AUTH_ACCOUNT_LOCKED(1006, "User account is locked", HttpStatus.UNAUTHORIZED),

    // User (2xxx)
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXISTS(2002, "Email already registered", HttpStatus.CONFLICT),
    USER_USERNAME_EXISTS(2003, "Username already taken", HttpStatus.CONFLICT),
    USER_INSUFFICIENT_POINTS(2004, "Insufficient loyalty points", HttpStatus.BAD_REQUEST),

    // Movie (3xxx)
    MOVIE_NOT_FOUND(3001, "Movie not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_SHOWING(3002, "Movie is not currently showing", HttpStatus.BAD_REQUEST),

    // Show (4xxx)
    SHOW_NOT_FOUND(4001, "Show not found", HttpStatus.NOT_FOUND),
    SHOW_ALREADY_STARTED(4002, "Show has already started", HttpStatus.BAD_REQUEST),
    SHOW_CANCELLED(4003, "Show has been cancelled", HttpStatus.BAD_REQUEST),
    SHOW_FULL(4004, "Show is fully booked", HttpStatus.BAD_REQUEST),
    SHOW_SCHEDULE_CONFLICT(4005, "Show schedule conflicts with existing show", HttpStatus.CONFLICT),
    SHOW_TIME_IN_PAST(4006, "Show time must be in the future", HttpStatus.BAD_REQUEST),
    SHOW_HAS_BOOKINGS(4007, "Cannot modify show with existing bookings", HttpStatus.CONFLICT),

    // Seat (5xxx)
    SEAT_NOT_FOUND(5001, "Seat not found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_LOCKED(5002, "Seat is locked by another user", HttpStatus.CONFLICT),
    SEAT_ALREADY_SOLD(5003, "Seat has already been sold", HttpStatus.CONFLICT),
    SEAT_LOCK_EXPIRED(5004, "Seat lock has expired", HttpStatus.BAD_REQUEST),
    SEAT_NOT_LOCKED_BY_USER(5005, "Seat is not locked by this user", HttpStatus.BAD_REQUEST),

    // Booking (6xxx)
    BOOKING_NOT_FOUND(6001, "Booking not found", HttpStatus.NOT_FOUND),
    BOOKING_ALREADY_CANCELLED(6002, "Booking already cancelled", HttpStatus.BAD_REQUEST),
    BOOKING_CANNOT_CANCEL(6003, "Booking cannot be cancelled", HttpStatus.BAD_REQUEST),
    BOOKING_PAYMENT_TIMEOUT(6004, "Payment timeout", HttpStatus.REQUEST_TIMEOUT),
    BOOKING_NO_SEATS(6005, "No seats selected", HttpStatus.BAD_REQUEST),

    // Payment (7xxx)
    PAYMENT_NOT_FOUND(7001, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_COMPLETED(7002, "Payment already completed", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED(7003, "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_SIGNATURE(7004, "Invalid payment signature", HttpStatus.BAD_REQUEST),

    // Cinema (8xxx)
    CINEMA_NOT_FOUND(8001, "Cinema not found", HttpStatus.NOT_FOUND),
    HALL_NOT_FOUND(8002, "Hall not found", HttpStatus.NOT_FOUND),

    // Validation & HTTP (9xxx)
    VALIDATION_ERROR(9001, "Validation error", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(9002, "Invalid request", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(9003, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(9004, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_NOT_FOUND(9005, "Resource not found", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE(9006, "Resource already exists", HttpStatus.CONFLICT),
    INVALID_STATE(9007, "Invalid state for this operation", HttpStatus.CONFLICT),

    // System (10xxx)
    INTERNAL_ERROR(10001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(10002, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    RATE_LIMIT_EXCEEDED(10003, "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    DATABASE_ERROR(10004, "Database error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

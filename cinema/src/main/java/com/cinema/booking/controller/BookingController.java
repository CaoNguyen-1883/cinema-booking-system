package com.cinema.booking.controller;

import com.cinema.booking.dto.*;
import com.cinema.booking.service.BookingService;
import com.cinema.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.cinema.user.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Bookings", description = "Booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seats")
    @Operation(summary = "Lock seats for booking",
            description = "Lock selected seats and create a pending booking. Booking expires in 15 minutes.")
    public ResponseEntity<ApiResponse<BookingResponse>> lockSeats(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LockSeatsRequest request) {

        BookingResponse booking = bookingService.lockSeats(user, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Seats locked successfully. Please complete payment within 15 minutes."));
    }

    @PostMapping("/{bookingId}/checkout")
    @Operation(summary = "Checkout booking",
            description = "Apply points discount and create payment URL")
    public ResponseEntity<ApiResponse<PaymentResponse>> checkout(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId,
            @Valid @RequestBody CheckoutRequest request) {

        PaymentResponse payment = bookingService.checkout(user.getId(), bookingId, request);

        return ResponseEntity.ok(ApiResponse.success(payment, "Checkout successful. Proceed to payment."));
    }

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm payment (mock)",
            description = "Confirm booking after payment success. Used for testing without real payment gateway.")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmPayment(
            @PathVariable Long bookingId,
            @RequestParam String transactionId) {

        BookingResponse booking = bookingService.confirmPayment(bookingId, transactionId);

        return ResponseEntity.ok(ApiResponse.success(booking, "Booking confirmed successfully!"));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel booking",
            description = "Cancel a pending booking and release locked seats")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {

        BookingResponse booking = bookingService.cancelBooking(user.getId(), bookingId);

        return ResponseEntity.ok(ApiResponse.success(booking, "Booking cancelled successfully"));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking details",
            description = "Get detailed information about a booking")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {

        BookingResponse booking = bookingService.getBookingById(user.getId(), bookingId);

        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/code/{bookingCode}")
    @Operation(summary = "Get booking by code",
            description = "Get booking information by booking code")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByCode(
            @PathVariable String bookingCode) {

        BookingResponse booking = bookingService.getBookingByCode(bookingCode);

        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get my bookings",
            description = "Get current user's booking history")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<BookingResponse> bookings = bookingService.getUserBookings(user.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/code/{bookingCode}/qr")
    @Operation(summary = "Get booking QR code",
            description = "Get QR code image for a confirmed booking")
    public ResponseEntity<byte[]> getBookingQRCode(@PathVariable String bookingCode) {
        byte[] qrCode = bookingService.getBookingQRCodeImage(bookingCode);

        if (qrCode == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCode.length);

        return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
    }

    @GetMapping("/code/{bookingCode}/qr-base64")
    @Operation(summary = "Get booking QR code as Base64",
            description = "Get QR code as Base64 string for a confirmed booking")
    public ResponseEntity<ApiResponse<String>> getBookingQRCodeBase64(@PathVariable String bookingCode) {
        String qrCode = bookingService.getBookingQRCodeBase64(bookingCode);

        if (qrCode == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "QR code not available"));
        }

        return ResponseEntity.ok(ApiResponse.success(qrCode, "QR code retrieved"));
    }
}

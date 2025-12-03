package com.cinema.payment.controller;

import com.cinema.payment.dto.*;
import com.cinema.payment.service.PaymentService;
import com.cinema.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Payment Controller
 * Handles payment creation, callbacks, and status queries
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create payment for a booking
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create payment", description = "Create payment URL for a booking")
    public ResponseEntity<ApiResponse<PaymentUrlResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest) {

        log.info("Creating payment for booking: {}", request.getBookingCode());
        PaymentUrlResponse response = paymentService.createPayment(request, httpRequest);

        return ResponseEntity.ok(ApiResponse.success(response, "Payment URL created successfully"));
    }

    /**
     * VNPay IPN callback (server-to-server)
     * This endpoint is called by VNPay to notify payment result
     */
    @GetMapping("/vnpay/ipn")
    @Operation(summary = "VNPay IPN callback", description = "Handle VNPay IPN notification")
    public String handleVNPayIPN(VNPayCallbackRequest callback) {
        log.info("Received VNPay IPN callback: txnRef={}", callback.getVnp_TxnRef());
        return paymentService.handleVNPayIPN(callback);
    }

    /**
     * VNPay return URL (redirect from VNPay after payment)
     * This endpoint handles user redirect after payment
     */
    @GetMapping("/vnpay/return")
    @Operation(summary = "VNPay return", description = "Handle VNPay return redirect")
    public ResponseEntity<ApiResponse<PaymentResultResponse>> handleVNPayReturn(VNPayCallbackRequest callback) {
        log.info("VNPay return callback: txnRef={}", callback.getVnp_TxnRef());
        PaymentResultResponse response = paymentService.handleVNPayReturn(callback);

        String message = response.isSuccess() ? "Payment successful" : response.getMessage();
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    /**
     * Get payment status by booking code
     */
    @GetMapping("/status/{bookingCode}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment status", description = "Get payment status by booking code")
    public ResponseEntity<ApiResponse<PaymentStatusResponse>> getPaymentStatus(
            @PathVariable String bookingCode) {

        log.info("Getting payment status for booking: {}", bookingCode);
        PaymentStatusResponse response = paymentService.getPaymentStatus(bookingCode);

        return ResponseEntity.ok(ApiResponse.success(response, "Payment status retrieved"));
    }
}

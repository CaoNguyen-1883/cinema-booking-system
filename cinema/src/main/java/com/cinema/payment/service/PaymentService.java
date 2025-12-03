package com.cinema.payment.service;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Booking.BookingStatus;
import com.cinema.booking.entity.Payment;
import com.cinema.booking.entity.Payment.PaymentMethod;
import com.cinema.booking.entity.Payment.PaymentStatus;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.PaymentRepository;
import com.cinema.payment.dto.*;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import com.cinema.shared.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Payment Service
 * Handles payment creation, processing, and callbacks
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final VNPayService vnPayService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    /**
     * Create payment for a booking
     */
    @Transactional
    public PaymentUrlResponse createPayment(CreatePaymentRequest request, HttpServletRequest httpRequest) {
        // Find booking
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Validate booking status
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_PENDING,
                    "Booking is not in PENDING status. Current status: " + booking.getStatus());
        }

        // Check if booking expired
        if (booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BOOKING_EXPIRED);
        }

        // Check for existing pending payment
        Optional<Payment> existingPayment = paymentRepository.findByBookingIdAndStatus(
                booking.getId(), PaymentStatus.PENDING);
        if (existingPayment.isPresent()) {
            // Return existing payment URL if still valid
            Payment payment = existingPayment.get();
            if (payment.getPaymentUrl() != null) {
                return PaymentUrlResponse.builder()
                        .paymentId(payment.getId())
                        .bookingCode(booking.getBookingCode())
                        .paymentUrl(payment.getPaymentUrl())
                        .expiryMinutes(15)
                        .paymentMethod(payment.getPaymentMethod().name())
                        .build();
            }
        }

        // Parse payment method
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Invalid payment method: " + request.getPaymentMethod());
        }

        // Create payment record
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(paymentMethod)
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        // Generate payment URL based on method
        String paymentUrl;
        switch (paymentMethod) {
            case VNPAY:
                paymentUrl = createVNPayPayment(payment, booking, request.getBankCode(), httpRequest);
                break;
            case MOMO:
            case ZALOPAY:
                // TODO: Implement other payment methods
                throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                        paymentMethod + " is not supported yet. Please use VNPAY.");
            case CASH:
                // Cash payment doesn't need URL
                paymentUrl = null;
                payment.setStatus(PaymentStatus.PROCESSING);
                break;
            default:
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Unknown payment method");
        }

        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);

        log.info("Payment created: {} for booking: {}", payment.getId(), booking.getBookingCode());

        return PaymentUrlResponse.builder()
                .paymentId(payment.getId())
                .bookingCode(booking.getBookingCode())
                .paymentUrl(paymentUrl)
                .expiryMinutes(15)
                .paymentMethod(paymentMethod.name())
                .build();
    }

    /**
     * Create VNPay payment URL
     */
    private String createVNPayPayment(Payment payment, Booking booking, String bankCode, HttpServletRequest httpRequest) {
        VNPayPaymentRequest vnpayRequest = VNPayPaymentRequest.builder()
                .orderId(booking.getBookingCode())
                .amount(booking.getTotalAmount().longValue())
                .orderInfo("Thanh toan ve xem phim - " + booking.getBookingCode())
                .bankCode(bankCode)
                .build();

        return vnPayService.createPaymentUrl(vnpayRequest, httpRequest);
    }

    /**
     * Handle VNPay IPN callback (server-to-server)
     */
    @Transactional
    public String handleVNPayIPN(VNPayCallbackRequest callback) {
        log.info("Received VNPay IPN for order: {}", callback.getVnp_TxnRef());

        // Verify signature
        if (!vnPayService.verifyCallback(callback)) {
            log.error("VNPay IPN signature verification failed");
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }

        // Find booking by order reference (booking code)
        String bookingCode = callback.getVnp_TxnRef();
        Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
        if (bookingOpt.isEmpty()) {
            log.error("Booking not found for VNPay IPN: {}", bookingCode);
            return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }

        Booking booking = bookingOpt.get();

        // Find pending payment
        Optional<Payment> paymentOpt = paymentRepository.findByBookingIdAndStatus(
                booking.getId(), PaymentStatus.PENDING);
        if (paymentOpt.isEmpty()) {
            log.warn("No pending payment found for booking: {}", bookingCode);
            return "{\"RspCode\":\"02\",\"Message\":\"Payment not found\"}";
        }

        Payment payment = paymentOpt.get();

        // Check amount
        long vnpayAmount = Long.parseLong(callback.getVnp_Amount()) / 100;
        if (vnpayAmount != payment.getAmount().longValue()) {
            log.error("Amount mismatch for payment: {} expected: {} received: {}",
                    payment.getId(), payment.getAmount(), vnpayAmount);
            return "{\"RspCode\":\"04\",\"Message\":\"Invalid amount\"}";
        }

        // Store callback data
        try {
            payment.setCallbackData(objectMapper.writeValueAsString(callback));
        } catch (Exception e) {
            log.warn("Failed to serialize callback data", e);
        }

        // Process payment result
        if (vnPayService.isPaymentSuccess(callback)) {
            processSuccessfulPayment(payment, booking, callback.getVnp_TransactionNo());
        } else {
            processFailedPayment(payment, booking,
                    vnPayService.getResponseMessage(callback.getVnp_ResponseCode()));
        }

        return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
    }

    /**
     * Handle VNPay return URL (redirect from VNPay)
     */
    @Transactional
    public PaymentResultResponse handleVNPayReturn(VNPayCallbackRequest callback) {
        log.info("VNPay return for order: {}", callback.getVnp_TxnRef());

        // Verify signature
        if (!vnPayService.verifyCallback(callback)) {
            return PaymentResultResponse.builder()
                    .success(false)
                    .bookingCode(callback.getVnp_TxnRef())
                    .message("Invalid signature")
                    .build();
        }

        String bookingCode = callback.getVnp_TxnRef();
        boolean isSuccess = vnPayService.isPaymentSuccess(callback);
        String message = vnPayService.getResponseMessage(callback.getVnp_ResponseCode());

        // Find booking
        Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
        if (bookingOpt.isEmpty()) {
            return PaymentResultResponse.builder()
                    .success(false)
                    .bookingCode(bookingCode)
                    .message("Booking not found")
                    .build();
        }

        Booking booking = bookingOpt.get();

        return PaymentResultResponse.builder()
                .success(isSuccess)
                .bookingCode(bookingCode)
                .transactionId(callback.getVnp_TransactionNo())
                .amount(booking.getTotalAmount())
                .message(message)
                .bookingStatus(booking.getStatus().name())
                .build();
    }

    /**
     * Process successful payment
     */
    private void processSuccessfulPayment(Payment payment, Booking booking, String transactionId) {
        log.info("Processing successful payment for booking: {}", booking.getBookingCode());

        // Update payment
        payment.complete(transactionId);
        paymentRepository.save(payment);

        // Update booking status
        booking.confirm();
        bookingRepository.save(booking);

        // Send confirmation email
        sendPaymentConfirmationEmail(booking, payment);

        log.info("Payment completed successfully: {} for booking: {}",
                payment.getId(), booking.getBookingCode());
    }

    /**
     * Process failed payment
     */
    private void processFailedPayment(Payment payment, Booking booking, String reason) {
        log.info("Processing failed payment for booking: {}", booking.getBookingCode());

        // Update payment
        payment.fail(reason);
        paymentRepository.save(payment);

        log.info("Payment failed: {} for booking: {} reason: {}",
                payment.getId(), booking.getBookingCode(), reason);
    }

    /**
     * Send payment confirmation email
     */
    private void sendPaymentConfirmationEmail(Booking booking, Payment payment) {
        try {
            // Get booking details
            Booking fullBooking = bookingRepository.findByIdWithDetails(booking.getId())
                    .orElse(booking);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            emailService.sendBookingConfirmation(
                    fullBooking.getUser().getEmail(),
                    fullBooking.getUser().getFullName(),
                    fullBooking.getBookingCode(),
                    fullBooking.getShow().getMovie().getTitle(),
                    fullBooking.getShow().getShowDate().format(dateFormatter),
                    fullBooking.getShow().getStartTime().format(timeFormatter),
                    fullBooking.getShow().getHall().getCinema().getName(),
                    fullBooking.getShow().getHall().getName(),
                    "Seats info", // TODO: Get actual seats
                    payment.getAmount().toString() + " VND",
                    null // TODO: Generate QR code
            );

            log.info("Payment confirmation email sent for booking: {}", booking.getBookingCode());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email for booking: {}",
                    booking.getBookingCode(), e);
        }
    }

    /**
     * Get payment by ID
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Payment not found"));
    }

    /**
     * Get payment status
     */
    public PaymentStatusResponse getPaymentStatus(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        Optional<Payment> latestPayment = paymentRepository.findByBookingIdAndStatus(
                booking.getId(), PaymentStatus.COMPLETED);

        if (latestPayment.isEmpty()) {
            latestPayment = paymentRepository.findByBookingIdAndStatus(
                    booking.getId(), PaymentStatus.PENDING);
        }

        if (latestPayment.isEmpty()) {
            return PaymentStatusResponse.builder()
                    .bookingCode(bookingCode)
                    .bookingStatus(booking.getStatus().name())
                    .paymentStatus("NO_PAYMENT")
                    .build();
        }

        Payment payment = latestPayment.get();
        return PaymentStatusResponse.builder()
                .bookingCode(bookingCode)
                .bookingStatus(booking.getStatus().name())
                .paymentStatus(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod().name())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .build();
    }
}

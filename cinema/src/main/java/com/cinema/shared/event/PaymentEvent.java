package com.cinema.shared.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment event for Kafka messaging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private PaymentData payment;
    private BookingData booking;
    private UserData user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        private Long paymentId;
        private String paymentMethod;
        private String status;
        private BigDecimal amount;
        private String transactionId;
        private String failureReason;
        private LocalDateTime paidAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingData {
        private Long bookingId;
        private String bookingCode;
        private String status;
        private String movieTitle;
        private String cinemaName;
        private String showDate;
        private String showTime;
        private Integer seatCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
    }

    // Event types
    public static final String TYPE_COMPLETED = "PAYMENT_COMPLETED";
    public static final String TYPE_FAILED = "PAYMENT_FAILED";
    public static final String TYPE_REFUNDED = "PAYMENT_REFUNDED";
}

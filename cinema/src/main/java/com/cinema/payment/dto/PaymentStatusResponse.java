package com.cinema.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response for payment status query
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {

    /**
     * Booking code
     */
    private String bookingCode;

    /**
     * Current booking status
     */
    private String bookingStatus;

    /**
     * Current payment status
     */
    private String paymentStatus;

    /**
     * Payment method used
     */
    private String paymentMethod;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Transaction ID from payment gateway
     */
    private String transactionId;

    /**
     * Time when payment was completed
     */
    private LocalDateTime paidAt;
}

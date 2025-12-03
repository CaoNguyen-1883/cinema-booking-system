package com.cinema.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response for payment result (return from VNPay)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultResponse {

    /**
     * Whether payment was successful
     */
    private boolean success;

    /**
     * Booking code
     */
    private String bookingCode;

    /**
     * Transaction ID from payment gateway
     */
    private String transactionId;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Result message
     */
    private String message;

    /**
     * Current booking status
     */
    private String bookingStatus;
}

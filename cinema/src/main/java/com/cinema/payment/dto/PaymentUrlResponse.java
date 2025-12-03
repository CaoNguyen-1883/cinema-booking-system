package com.cinema.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing payment URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlResponse {

    /**
     * Payment ID in our system
     */
    private Long paymentId;

    /**
     * Booking code
     */
    private String bookingCode;

    /**
     * Payment URL to redirect user
     */
    private String paymentUrl;

    /**
     * Expiry time in minutes
     */
    private int expiryMinutes;

    /**
     * Payment method
     */
    private String paymentMethod;
}

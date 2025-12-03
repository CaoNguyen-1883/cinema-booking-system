package com.cinema.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create payment for a booking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    /**
     * Booking code to pay for
     */
    @NotBlank(message = "Booking code is required")
    private String bookingCode;

    /**
     * Payment method (VNPAY, MOMO, etc.)
     */
    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    /**
     * Bank code (optional - for VNPay)
     */
    private String bankCode;
}

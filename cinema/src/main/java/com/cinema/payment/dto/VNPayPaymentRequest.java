package com.cinema.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create VNPay payment URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayPaymentRequest {

    /**
     * Unique order ID (booking code)
     */
    private String orderId;

    /**
     * Amount in VND (not multiplied by 100)
     */
    private long amount;

    /**
     * Order information/description
     */
    private String orderInfo;

    /**
     * Bank code (optional - NCB, VIETCOMBANK, etc.)
     * If empty, user will choose on VNPay page
     */
    private String bankCode;
}

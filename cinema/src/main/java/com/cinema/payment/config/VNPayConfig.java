package com.cinema.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * VNPay Configuration Properties
 * Sandbox credentials for testing
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    
    /**
     * VNPay payment URL
     */
    private String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    
    /**
     * VNPay API URL for query transactions
     */
    private String apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    /**
     * Terminal ID (provided by VNPay)
     */
    private String tmnCode = "DEMOV210";
    
    /**
     * Secret key for HMAC SHA512 signature
     */
    private String hashSecret = "RAOEXHYVSDDIIENYWSLDIIZTANXUXZFJ";
    
    /**
     * Return URL after payment (frontend URL)
     */
    private String returnUrl = "http://localhost:3000/payment/result";
    
    /**
     * IPN URL for server-to-server callback
     */
    private String ipnUrl = "http://localhost:9000/api/payments/vnpay/ipn";
    
    /**
     * API version
     */
    private String version = "2.1.0";
    
    /**
     * Command for payment
     */
    private String command = "pay";
    
    /**
     * Order type
     */
    private String orderType = "other";
    
    /**
     * Currency code (VND)
     */
    private String currencyCode = "VND";
    
    /**
     * Locale (vn or en)
     */
    private String locale = "vn";
}

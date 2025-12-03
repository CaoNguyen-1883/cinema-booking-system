package com.cinema.payment.service;

import com.cinema.payment.config.VNPayConfig;
import com.cinema.payment.dto.VNPayCallbackRequest;
import com.cinema.payment.dto.VNPayPaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VNPay Payment Gateway Service
 * Handles payment URL creation and callback verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final VNPayConfig vnPayConfig;

    /**
     * Create VNPay payment URL
     */
    public String createPaymentUrl(VNPayPaymentRequest request, HttpServletRequest httpRequest) {
        String vnp_TxnRef = request.getOrderId();
        long amount = request.getAmount() * 100; // VNPay requires amount in VND * 100

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
        vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", vnPayConfig.getCurrencyCode());
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", request.getOrderInfo());
        vnp_Params.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnp_Params.put("vnp_Locale", vnPayConfig.getLocale());
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", getIpAddress(httpRequest));

        // Create date
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Expire time (15 minutes)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Bank code (optional - let user choose on VNPay page)
        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", request.getBankCode());
        }

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnPayConfig.getPayUrl() + "?" + queryUrl;
        log.info("VNPay payment URL created for order: {}", vnp_TxnRef);

        return paymentUrl;
    }

    /**
     * Verify VNPay callback signature
     */
    public boolean verifyCallback(VNPayCallbackRequest callback) {
        String vnp_SecureHash = callback.getVnp_SecureHash();

        // Remove hash params for verification
        Map<String, String> fields = callback.toMap();
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // Sort and build hash data
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        boolean isValid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);

        if (!isValid) {
            log.warn("VNPay callback signature verification failed for order: {}", callback.getVnp_TxnRef());
        }

        return isValid;
    }

    /**
     * Check if payment is successful
     */
    public boolean isPaymentSuccess(VNPayCallbackRequest callback) {
        return "00".equals(callback.getVnp_ResponseCode()) &&
               "00".equals(callback.getVnp_TransactionStatus());
    }

    /**
     * Get response message from VNPay response code
     */
    public String getResponseMessage(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Giao dich thanh cong";
            case "07" -> "Tru tien thanh cong. Giao dich bi nghi ngo";
            case "09" -> "The/Tai khoan chua dang ky InternetBanking";
            case "10" -> "Xac thuc thong tin the/tai khoan khong dung qua 3 lan";
            case "11" -> "Da het han cho thanh toan";
            case "12" -> "The/Tai khoan bi khoa";
            case "13" -> "Nhap sai mat khau OTP";
            case "24" -> "Khach hang huy giao dich";
            case "51" -> "Tai khoan khong du so du";
            case "65" -> "Tai khoan da vuot qua han muc giao dich trong ngay";
            case "75" -> "Ngan hang thanh toan dang bao tri";
            case "79" -> "Nhap sai mat khau thanh toan qua so lan quy dinh";
            case "99" -> "Cac loi khac";
            default -> "Giao dich that bai - Ma loi: " + responseCode;
        };
    }

    /**
     * HMAC SHA512 hash
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error creating HMAC SHA512 hash", e);
            throw new RuntimeException("Error creating hash", e);
        }
    }

    /**
     * Get client IP address
     */
    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // Handle multiple IPs
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress != null ? ipAddress : "127.0.0.1";
    }
}

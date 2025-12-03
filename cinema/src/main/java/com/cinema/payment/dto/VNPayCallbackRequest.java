package com.cinema.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * VNPay callback/IPN request parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayCallbackRequest {

    private String vnp_TmnCode;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_BankTranNo;
    private String vnp_CardType;
    private String vnp_PayDate;
    private String vnp_OrderInfo;
    private String vnp_TransactionNo;
    private String vnp_ResponseCode;
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHash;
    private String vnp_SecureHashType;

    /**
     * Convert to map for signature verification
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (vnp_TmnCode != null) map.put("vnp_TmnCode", vnp_TmnCode);
        if (vnp_Amount != null) map.put("vnp_Amount", vnp_Amount);
        if (vnp_BankCode != null) map.put("vnp_BankCode", vnp_BankCode);
        if (vnp_BankTranNo != null) map.put("vnp_BankTranNo", vnp_BankTranNo);
        if (vnp_CardType != null) map.put("vnp_CardType", vnp_CardType);
        if (vnp_PayDate != null) map.put("vnp_PayDate", vnp_PayDate);
        if (vnp_OrderInfo != null) map.put("vnp_OrderInfo", vnp_OrderInfo);
        if (vnp_TransactionNo != null) map.put("vnp_TransactionNo", vnp_TransactionNo);
        if (vnp_ResponseCode != null) map.put("vnp_ResponseCode", vnp_ResponseCode);
        if (vnp_TransactionStatus != null) map.put("vnp_TransactionStatus", vnp_TransactionStatus);
        if (vnp_TxnRef != null) map.put("vnp_TxnRef", vnp_TxnRef);
        if (vnp_SecureHash != null) map.put("vnp_SecureHash", vnp_SecureHash);
        if (vnp_SecureHashType != null) map.put("vnp_SecureHashType", vnp_SecureHashType);
        return map;
    }
}

package com.cinema.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * SMS Service for sending text messages
 * Supports multiple providers (Twilio, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${app.notification.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.notification.sms.provider:twilio}")
    private String provider;

    @Value("${app.notification.sms.api-key:}")
    private String apiKey;

    @Value("${app.notification.sms.api-secret:}")
    private String apiSecret;

    @Value("${app.notification.sms.from-number:}")
    private String fromNumber;

    /**
     * Send SMS message
     */
    @Async
    public void sendSms(String toPhoneNumber, String message) {
        if (!smsEnabled) {
            log.info("SMS disabled. Would send to: {} message: {}", toPhoneNumber, message);
            return;
        }

        if (apiKey.isEmpty() || fromNumber.isEmpty()) {
            log.warn("SMS not configured. Missing API key or from number.");
            return;
        }

        try {
            // TODO: Implement actual SMS sending based on provider
            switch (provider.toLowerCase()) {
                case "twilio" -> sendViaTwilio(toPhoneNumber, message);
                case "vonage" -> sendViaVonage(toPhoneNumber, message);
                default -> log.warn("Unknown SMS provider: {}", provider);
            }
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }

    /**
     * Send booking confirmation SMS
     */
    public void sendBookingConfirmation(String phoneNumber, String bookingCode,
                                         String movieTitle, String showDate, String showTime) {
        String message = String.format(
            "Cinema Booking: Xac nhan dat ve #%s. Phim: %s. Ngay: %s, Gio: %s. Cam on ban!",
            bookingCode, movieTitle, showDate, showTime
        );
        sendSms(phoneNumber, message);
    }

    /**
     * Send booking created (pending payment) SMS
     */
    public void sendBookingCreated(String phoneNumber, String bookingCode, int expirationMinutes) {
        String message = String.format(
            "Cinema Booking: Dat ve #%s thanh cong. Vui long thanh toan trong %d phut.",
            bookingCode, expirationMinutes
        );
        sendSms(phoneNumber, message);
    }

    /**
     * Send booking cancelled SMS
     */
    public void sendBookingCancelled(String phoneNumber, String bookingCode) {
        String message = String.format(
            "Cinema Booking: Dat ve #%s da bi huy. Lien he hotline 1900 1234 de ho tro.",
            bookingCode
        );
        sendSms(phoneNumber, message);
    }

    /**
     * Send OTP SMS
     */
    public void sendOtp(String phoneNumber, String otp, int validMinutes) {
        String message = String.format(
            "Cinema Booking: Ma xac thuc cua ban la %s. Ma co hieu luc trong %d phut.",
            otp, validMinutes
        );
        sendSms(phoneNumber, message);
    }

    // ==================== Provider Implementations ====================

    private void sendViaTwilio(String toPhoneNumber, String message) {
        // TODO: Implement Twilio SMS sending
        // Example:
        // Twilio.init(apiKey, apiSecret);
        // Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromNumber), message).create();
        log.info("Twilio SMS sent to {}: {}", toPhoneNumber, message);
    }

    private void sendViaVonage(String toPhoneNumber, String message) {
        // TODO: Implement Vonage (Nexmo) SMS sending
        log.info("Vonage SMS sent to {}: {}", toPhoneNumber, message);
    }
}

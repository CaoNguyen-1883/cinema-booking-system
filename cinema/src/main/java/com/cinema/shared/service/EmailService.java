package com.cinema.shared.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Email Service for sending transactional emails
 * Supports HTML templates with Thymeleaf
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.notification.email.from-name:Cinema Booking}")
    private String fromName;

    @Value("${app.notification.email.from-address:noreply@cinema.com}")
    private String fromAddress;

    /**
     * Send a simple text email
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send to: {} with subject: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Send HTML email using Thymeleaf template
     */
    @Async
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send template {} to: {} with subject: {}", templateName, to, subject);
            return;
        }

        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Template email '{}' sent successfully to: {}", templateName, to);
        } catch (Exception e) {
            log.error("Failed to send template email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(String to, String customerName, String bookingCode,
                                         String movieTitle, String showDate, String showTime,
                                         String cinemaName, String hallName, String seats,
                                         String totalAmount, String qrCodeBase64) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "bookingCode", bookingCode,
            "movieTitle", movieTitle,
            "showDate", showDate,
            "showTime", showTime,
            "cinemaName", cinemaName,
            "hallName", hallName,
            "seats", seats,
            "totalAmount", totalAmount,
            "qrCode", qrCodeBase64 != null ? qrCodeBase64 : ""
        );

        sendTemplateEmail(to, "Xác nhận đặt vé - " + bookingCode, "booking-confirmation", variables);
    }

    /**
     * Send booking created (pending payment) email
     */
    public void sendBookingCreated(String to, String customerName, String bookingCode,
                                    String movieTitle, String showDate, String showTime,
                                    String totalAmount, int expirationMinutes) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "bookingCode", bookingCode,
            "movieTitle", movieTitle,
            "showDate", showDate,
            "showTime", showTime,
            "totalAmount", totalAmount,
            "expirationMinutes", expirationMinutes
        );

        sendTemplateEmail(to, "Đặt vé thành công - Vui lòng thanh toán", "booking-created", variables);
    }

    /**
     * Send booking cancelled email
     */
    public void sendBookingCancelled(String to, String customerName, String bookingCode,
                                      String movieTitle, String reason) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "bookingCode", bookingCode,
            "movieTitle", movieTitle,
            "reason", reason != null ? reason : "Theo yêu cầu của khách hàng"
        );

        sendTemplateEmail(to, "Đặt vé đã bị hủy - " + bookingCode, "booking-cancelled", variables);
    }

    /**
     * Send booking expired email
     */
    public void sendBookingExpired(String to, String customerName, String bookingCode,
                                    String movieTitle) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "bookingCode", bookingCode,
            "movieTitle", movieTitle
        );

        sendTemplateEmail(to, "Đặt vé đã hết hạn - " + bookingCode, "booking-expired", variables);
    }

    /**
     * Send password reset email
     */
    public void sendPasswordReset(String to, String customerName, String resetLink) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName,
            "resetLink", resetLink
        );

        sendTemplateEmail(to, "Yêu cầu đặt lại mật khẩu", "password-reset", variables);
    }

    /**
     * Send welcome email after registration
     */
    public void sendWelcomeEmail(String to, String customerName) {
        Map<String, Object> variables = Map.of(
            "customerName", customerName
        );

        sendTemplateEmail(to, "Chào mừng bạn đến với Cinema Booking!", "welcome", variables);
    }
}

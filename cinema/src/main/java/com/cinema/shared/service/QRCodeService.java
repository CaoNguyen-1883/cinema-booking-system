package com.cinema.shared.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code Generation Service
 * Generates QR codes for booking tickets
 */
@Service
@Slf4j
public class QRCodeService {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String IMAGE_FORMAT = "PNG";

    /**
     * Generate QR code as Base64 encoded string
     * @param content Content to encode in QR code
     * @return Base64 encoded PNG image
     */
    public String generateQRCodeBase64(String content) {
        return generateQRCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Generate QR code as Base64 encoded string with custom dimensions
     * @param content Content to encode
     * @param width Width in pixels
     * @param height Height in pixels
     * @return Base64 encoded PNG image
     */
    public String generateQRCodeBase64(String content, int width, int height) {
        try {
            byte[] qrCodeBytes = generateQRCodeBytes(content, width, height);
            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            log.error("Failed to generate QR code for content: {}", content, e);
            return null;
        }
    }

    /**
     * Generate QR code as byte array
     * @param content Content to encode
     * @param width Width in pixels
     * @param height Height in pixels
     * @return PNG image as byte array
     */
    public byte[] generateQRCodeBytes(String content, int width, int height)
            throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Generate booking QR code content
     * Contains booking info for scanning at cinema
     * @param bookingCode Booking code
     * @param movieTitle Movie title
     * @param showDate Show date
     * @param showTime Show time
     * @param seats Seat codes
     * @param cinemaName Cinema name
     * @return Formatted QR content
     */
    public String generateBookingQRContent(
            String bookingCode,
            String movieTitle,
            String showDate,
            String showTime,
            String seats,
            String cinemaName) {

        // JSON format for easy parsing
        return String.format(
            "{\"code\":\"%s\",\"movie\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"seats\":\"%s\",\"cinema\":\"%s\"}",
            bookingCode,
            escapeJson(movieTitle),
            showDate,
            showTime,
            seats,
            escapeJson(cinemaName)
        );
    }

    /**
     * Generate simple booking QR code (just booking code)
     * For quick verification at cinema
     * @param bookingCode Booking code
     * @return Base64 encoded QR code
     */
    public String generateBookingQRCode(String bookingCode) {
        return generateQRCodeBase64(bookingCode);
    }

    /**
     * Generate full booking QR code with details
     * @param bookingCode Booking code
     * @param movieTitle Movie title
     * @param showDate Show date
     * @param showTime Show time
     * @param seats Seat codes
     * @param cinemaName Cinema name
     * @return Base64 encoded QR code
     */
    public String generateFullBookingQRCode(
            String bookingCode,
            String movieTitle,
            String showDate,
            String showTime,
            String seats,
            String cinemaName) {

        String content = generateBookingQRContent(
            bookingCode, movieTitle, showDate, showTime, seats, cinemaName
        );

        log.info("Generating QR code for booking: {}", bookingCode);
        return generateQRCodeBase64(content);
    }

    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

package com.cinema.booking.service;

import com.cinema.booking.dto.*;
import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Booking.BookingStatus;
import com.cinema.booking.entity.BookingSeat;
import com.cinema.booking.entity.Payment;
import com.cinema.booking.entity.Payment.PaymentMethod;
import com.cinema.booking.entity.Payment.PaymentStatus;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.BookingSeatRepository;
import com.cinema.booking.repository.PaymentRepository;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import com.cinema.show.entity.Show;
import com.cinema.show.entity.Show.ShowStatus;
import com.cinema.show.entity.ShowSeat;
import com.cinema.show.entity.ShowSeat.ShowSeatStatus;
import com.cinema.show.repository.ShowRepository;
import com.cinema.show.repository.ShowSeatRepository;
import com.cinema.user.entity.User;
import com.cinema.user.repository.UserRepository;
import com.cinema.shared.service.RedisLockService;
import com.cinema.shared.service.KafkaProducerService;
import com.cinema.shared.service.QRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentRepository paymentRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final RedisLockService redisLockService;
    private final KafkaProducerService kafkaProducerService;
    private final QRCodeService qrCodeService;

    // Booking expires after 15 minutes
    private static final int BOOKING_EXPIRY_MINUTES = 15;
    // Max seats per booking
    private static final int MAX_SEATS_PER_BOOKING = 10;
    // Points conversion: 1000 VND = 1 point
    private static final BigDecimal POINTS_CONVERSION_RATE = new BigDecimal("1000");
    // Max discount from points: 50%
    private static final BigDecimal MAX_POINTS_DISCOUNT_PERCENT = new BigDecimal("0.5");

    /**
     * Lock seats and create a pending booking
     * Uses Redis distributed lock to prevent race conditions
     */
    @Transactional
    public BookingResponse lockSeats(User user, LockSeatsRequest request) {
        // Validate seat count
        if (request.getShowSeatIds().size() > MAX_SEATS_PER_BOOKING) {
            throw new BusinessException(ErrorCode.BOOKING_MAX_SEATS_EXCEEDED);
        }

        Long userId = user.getId();

        // Get show
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_NOT_FOUND));

        // Validate show is scheduled
        if (show.getStatus() != ShowStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.SHOW_CANCELLED);
        }

        // Validate show hasn't started
        LocalDateTime showDateTime = LocalDateTime.of(show.getShowDate(), show.getStartTime());
        if (showDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SHOW_ALREADY_STARTED);
        }

        // Check if user already has pending booking for this show
        if (bookingRepository.existsPendingBooking(userId, request.getShowId())) {
            throw new BusinessException(ErrorCode.BOOKING_ALREADY_EXISTS);
        }

        // Try to acquire Redis distributed lock for all seats first
        // This prevents race conditions when multiple users try to book the same seat
        long lockTimeoutSeconds = BOOKING_EXPIRY_MINUTES * 60L;
        if (!redisLockService.tryLockSeats(request.getShowSeatIds(), userId, lockTimeoutSeconds)) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_LOCKED);
        }

        try {
            // Get and validate show seats
            List<ShowSeat> showSeats = showSeatRepository.findAllById(request.getShowSeatIds());
            if (showSeats.size() != request.getShowSeatIds().size()) {
                throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
            }

            // Lock seats and calculate total
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<ShowSeat> lockedSeats = new ArrayList<>();

            for (ShowSeat showSeat : showSeats) {
                // Validate seat belongs to the show
                if (!showSeat.getShow().getId().equals(request.getShowId())) {
                    throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
                }

                // Check if seat is available (double-check after Redis lock)
                if (showSeat.getStatus() == ShowSeatStatus.SOLD) {
                    throw new BusinessException(ErrorCode.SEAT_ALREADY_SOLD);
                }
                if (showSeat.getStatus() == ShowSeatStatus.LOCKED) {
                    throw new BusinessException(ErrorCode.SEAT_ALREADY_LOCKED);
                }

                // Lock the seat in database
                showSeat.lock(user);
                lockedSeats.add(showSeat);
                totalAmount = totalAmount.add(showSeat.getPrice());
            }

            // Save locked seats
            showSeatRepository.saveAll(lockedSeats);

            // Create booking
            String bookingCode = generateBookingCode();
            Booking booking = Booking.builder()
                    .bookingCode(bookingCode)
                    .user(user)
                    .show(show)
                    .totalAmount(totalAmount)
                    .discountAmount(BigDecimal.ZERO)
                    .finalAmount(totalAmount)
                    .pointsUsed(0)
                    .pointsEarned(0)
                    .status(BookingStatus.PENDING)
                    .expiresAt(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES))
                    .bookingSeats(new ArrayList<>())
                    .payments(new ArrayList<>())
                    .build();

            // Create booking seats
            for (ShowSeat showSeat : lockedSeats) {
                BookingSeat bookingSeat = BookingSeat.builder()
                        .showSeat(showSeat)
                        .price(showSeat.getPrice())
                        .build();
                booking.addBookingSeat(bookingSeat);
            }

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Created booking {} for user {} with {} seats, total: {}",
                    bookingCode, userId, lockedSeats.size(), totalAmount);

            // Publish booking created event to Kafka
            kafkaProducerService.publishBookingCreated(savedBooking);

            return BookingResponse.fromEntity(savedBooking);

        } catch (Exception e) {
            // Release Redis locks if database operation fails
            redisLockService.unlockSeats(request.getShowSeatIds(), userId);
            throw e;
        }
    }

    /**
     * Checkout - apply points and create payment
     */
    @Transactional
    public PaymentResponse checkout(Long userId, Long bookingId, CheckoutRequest request) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Validate booking belongs to user
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
        }

        // Validate booking is pending
        if (!booking.isPending()) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_PENDING);
        }

        // Check if booking expired
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BOOKING_EXPIRED);
        }

        // Apply points discount
        BigDecimal finalAmount = booking.getTotalAmount();
        int pointsToUse = request.getPointsToUse();

        if (pointsToUse > 0) {
            User user = booking.getUser();

            // Validate user has enough points
            if (user.getPoints() < pointsToUse) {
                throw new BusinessException(ErrorCode.USER_INSUFFICIENT_POINTS);
            }

            // Calculate max discount (50% of total)
            BigDecimal maxDiscount = booking.getTotalAmount().multiply(MAX_POINTS_DISCOUNT_PERCENT);
            BigDecimal pointsDiscount = new BigDecimal(pointsToUse).multiply(POINTS_CONVERSION_RATE);

            // Apply actual discount (min of requested and max allowed)
            BigDecimal actualDiscount = pointsDiscount.min(maxDiscount);
            finalAmount = finalAmount.subtract(actualDiscount);

            // Update booking
            booking.setDiscountAmount(actualDiscount);
            booking.setPointsUsed(actualDiscount.divide(POINTS_CONVERSION_RATE, 0, BigDecimal.ROUND_DOWN).intValue());
        }

        booking.setFinalAmount(finalAmount);

        // Create payment record
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(request.getPaymentMethod())
                .amount(finalAmount)
                .status(PaymentStatus.PENDING)
                .build();

        // Generate payment URL (placeholder - will integrate with VNPay/Momo later)
        String paymentUrl = generatePaymentUrl(booking, payment);
        payment.setPaymentUrl(paymentUrl);

        booking.getPayments().add(payment);
        bookingRepository.save(booking);

        log.info("Checkout booking {} with payment method {}, amount: {}",
                booking.getBookingCode(), request.getPaymentMethod(), finalAmount);

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Confirm booking after successful payment
     */
    @Transactional
    public BookingResponse confirmPayment(Long bookingId, String transactionId) {
        Booking booking = bookingRepository.findByIdWithSeats(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.isPending()) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_PENDING);
        }

        // Find pending payment
        Payment payment = booking.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        // Complete payment
        payment.complete(transactionId);

        // Confirm booking
        booking.confirm();

        // Calculate points earned
        int pointsEarned = booking.getFinalAmount()
                .divide(POINTS_CONVERSION_RATE, 0, BigDecimal.ROUND_DOWN)
                .intValue();
        booking.setPointsEarned(pointsEarned);

        // Update user points
        User user = booking.getUser();
        user.setPoints(user.getPoints() - booking.getPointsUsed() + pointsEarned);
        userRepository.save(user);

        // Mark seats as sold
        for (BookingSeat bs : booking.getBookingSeats()) {
            ShowSeat showSeat = bs.getShowSeat();
            showSeat.sell();
            showSeatRepository.save(showSeat);
        }

        // Generate QR code (placeholder)
        String qrCode = generateQRCode(booking);
        booking.setQrCode(qrCode);

        Booking confirmedBooking = bookingRepository.save(booking);
        log.info("Confirmed booking {} with transaction {}", booking.getBookingCode(), transactionId);

        // Publish booking confirmed event to Kafka
        kafkaProducerService.publishBookingConfirmed(confirmedBooking);

        return BookingResponse.fromEntity(confirmedBooking);
    }

    /**
     * Cancel booking
     */
    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdWithSeats(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Validate booking belongs to user
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
        }

        // Can only cancel pending bookings
        if (!booking.isPending()) {
            throw new BusinessException(ErrorCode.BOOKING_CANNOT_CANCEL);
        }

        // Cancel booking
        booking.cancel();

        // Release locked seats (both Redis and database)
        List<Long> seatIds = new ArrayList<>();
        for (BookingSeat bs : booking.getBookingSeats()) {
            ShowSeat showSeat = bs.getShowSeat();
            seatIds.add(showSeat.getId());
            showSeat.unlock();
            showSeatRepository.save(showSeat);
        }

        // Release Redis locks
        redisLockService.unlockSeats(seatIds, userId);

        Booking cancelledBooking = bookingRepository.save(booking);
        log.info("Cancelled booking {}", booking.getBookingCode());

        // Publish booking cancelled event to Kafka
        kafkaProducerService.publishBookingCancelled(cancelledBooking);

        return BookingResponse.fromEntity(cancelledBooking);
    }

    /**
     * Get booking by ID
     */
    public BookingResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Validate booking belongs to user
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
        }

        // Load seats
        Booking bookingWithSeats = bookingRepository.findByIdWithSeats(bookingId).orElse(booking);

        return BookingResponse.fromEntity(bookingWithSeats);
    }

    /**
     * Get booking by code
     */
    public BookingResponse getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        return BookingResponse.fromEntity(booking);
    }

    /**
     * Get user's booking history
     */
    public Page<BookingResponse> getUserBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserIdWithDetails(userId, pageable)
                .map(BookingResponse::fromEntityBasic);
    }

    /**
     * Scheduled task: Expire pending bookings
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void expireBookings() {
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            try {
                // Load seats
                Booking bookingWithSeats = bookingRepository.findByIdWithSeats(booking.getId())
                        .orElse(booking);

                // Expire booking
                bookingWithSeats.expire();

                // Release locked seats (both Redis and database)
                for (BookingSeat bs : bookingWithSeats.getBookingSeats()) {
                    ShowSeat showSeat = bs.getShowSeat();
                    if (showSeat.getStatus() == ShowSeatStatus.LOCKED) {
                        // Force release Redis lock (system cleanup)
                        redisLockService.forceUnlockSeat(showSeat.getId());
                        showSeat.unlock();
                        showSeatRepository.save(showSeat);
                    }
                }

                bookingRepository.save(bookingWithSeats);
                log.info("Expired booking {}", bookingWithSeats.getBookingCode());

                // Publish booking expired event to Kafka
                kafkaProducerService.publishBookingExpired(bookingWithSeats);

            } catch (Exception e) {
                log.error("Error expiring booking {}: {}", booking.getId(), e.getMessage());
            }
        }

        if (!expiredBookings.isEmpty()) {
            log.info("Expired {} bookings", expiredBookings.size());
        }
    }

    // ===================== Helper Methods =====================

    private String generateBookingCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "BK" + timestamp + random;
    }

    private String generatePaymentUrl(Booking booking, Payment payment) {
        // Placeholder - will integrate with VNPay/Momo
        return String.format("/api/payment/mock?bookingId=%d&amount=%s&method=%s",
                booking.getId(), payment.getAmount(), payment.getPaymentMethod());
    }

    private String generateQRCode(Booking booking) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String seats = booking.getBookingSeats().stream()
                    .map(bs -> bs.getShowSeat().getSeat().getRowName() + bs.getShowSeat().getSeat().getSeatNumber())
                    .collect(Collectors.joining(", "));

            return qrCodeService.generateFullBookingQRCode(
                    booking.getBookingCode(),
                    booking.getShow().getMovie().getTitle(),
                    booking.getShow().getShowDate().format(dateFormatter),
                    booking.getShow().getStartTime().format(timeFormatter),
                    seats,
                    booking.getShow().getHall().getCinema().getName()
            );
        } catch (Exception e) {
            log.error("Failed to generate QR code for booking: {}", booking.getBookingCode(), e);
            // Fallback to simple QR code with just booking code
            return qrCodeService.generateBookingQRCode(booking.getBookingCode());
        }
    }

    /**
     * Get QR code image for a booking
     * @param bookingCode The booking code
     * @return QR code as PNG byte array, or null if not available
     */
    public byte[] getBookingQRCodeImage(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Only return QR for confirmed bookings
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return null;
        }

        // If QR code is stored as base64, decode it
        if (booking.getQrCode() != null && !booking.getQrCode().isEmpty()) {
            try {
                return java.util.Base64.getDecoder().decode(booking.getQrCode());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid base64 QR code for booking: {}", bookingCode);
            }
        }

        // Generate QR code on-the-fly if not stored
        try {
            return qrCodeService.generateQRCodeBytes(bookingCode, 300, 300);
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code image for booking: {}", bookingCode, e);
            return null;
        }
    }

    /**
     * Get QR code as Base64 string for a booking
     * @param bookingCode The booking code
     * @return QR code as Base64 string, or null if not available
     */
    public String getBookingQRCodeBase64(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Only return QR for confirmed bookings
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return null;
        }

        // Return stored QR code if available
        if (booking.getQrCode() != null && !booking.getQrCode().isEmpty()) {
            return booking.getQrCode();
        }

        // Generate on-the-fly
        return qrCodeService.generateBookingQRCode(bookingCode);
    }
}

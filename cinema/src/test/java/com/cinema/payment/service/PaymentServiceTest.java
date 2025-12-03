package com.cinema.payment.service;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Booking.BookingStatus;
import com.cinema.booking.entity.Payment;
import com.cinema.booking.entity.Payment.PaymentMethod;
import com.cinema.booking.entity.Payment.PaymentStatus;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.PaymentRepository;
import com.cinema.payment.dto.*;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.service.EmailService;
import com.cinema.shared.service.KafkaProducerService;
import com.cinema.shared.service.QRCodeService;
import com.cinema.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private VNPayService vnPayService;
    @Mock
    private EmailService emailService;
    @Mock
    private QRCodeService qrCodeService;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private PaymentService paymentService;

    private Booking testBooking;
    private Payment testPayment;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        testBooking = Booking.builder()
                .id(1L)
                .bookingCode("BK2401011200ABCD")
                .user(testUser)
                .totalAmount(new BigDecimal("100000"))
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(new BigDecimal("100000"))
                .status(BookingStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .bookingSeats(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();

        testPayment = Payment.builder()
                .id(1L)
                .booking(testBooking)
                .paymentMethod(PaymentMethod.VNPAY)
                .amount(new BigDecimal("100000"))
                .status(PaymentStatus.PENDING)
                .paymentUrl("http://vnpay.vn/pay?txn=123")
                .build();
    }

    @Nested
    @DisplayName("Create Payment Tests")
    class CreatePaymentTests {

        @Test
        @DisplayName("Should create VNPay payment successfully")
        void createPayment_VNPay_Success() {
            // Given
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("BK2401011200ABCD")
                    .paymentMethod("VNPAY")
                    .build();

            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.PENDING)))
                    .thenReturn(Optional.empty());
            when(vnPayService.createPaymentUrl(any(VNPayPaymentRequest.class), any(HttpServletRequest.class)))
                    .thenReturn("http://vnpay.vn/pay?txn=123");
            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

            // When
            PaymentUrlResponse response = paymentService.createPayment(request, httpRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentUrl()).isNotNull();
            assertThat(response.getBookingCode()).isEqualTo("BK2401011200ABCD");
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("Should return existing payment URL if exists")
        void createPayment_ExistingPayment_ReturnsExistingUrl() {
            // Given
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("BK2401011200ABCD")
                    .paymentMethod("VNPAY")
                    .build();

            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.PENDING)))
                    .thenReturn(Optional.of(testPayment));

            // When
            PaymentUrlResponse response = paymentService.createPayment(request, httpRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPaymentUrl()).isEqualTo("http://vnpay.vn/pay?txn=123");
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void createPayment_BookingNotFound_ThrowsException() {
            // Given
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("INVALID")
                    .paymentMethod("VNPAY")
                    .build();
            when(bookingRepository.findByBookingCode("INVALID")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request, httpRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when booking not pending")
        void createPayment_BookingNotPending_ThrowsException() {
            // Given
            testBooking.setStatus(BookingStatus.CONFIRMED);
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("BK2401011200ABCD")
                    .paymentMethod("VNPAY")
                    .build();
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request, httpRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when booking expired")
        void createPayment_BookingExpired_ThrowsException() {
            // Given
            testBooking.setExpiresAt(LocalDateTime.now().minusMinutes(1));
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("BK2401011200ABCD")
                    .paymentMethod("VNPAY")
                    .build();
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request, httpRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception for unsupported payment method")
        void createPayment_UnsupportedMethod_ThrowsException() {
            // Given
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .bookingCode("BK2401011200ABCD")
                    .paymentMethod("MOMO")
                    .build();
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.PENDING)))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request, httpRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("VNPay IPN Tests")
    class VNPayIPNTests {

        @Test
        @DisplayName("Should handle successful VNPay IPN")
        void handleVNPayIPN_Success() {
            // Given
            VNPayCallbackRequest callback = VNPayCallbackRequest.builder()
                    .vnp_TxnRef("BK2401011200ABCD")
                    .vnp_Amount("10000000") // 100000 * 100
                    .vnp_ResponseCode("00")
                    .vnp_TransactionNo("TXN123")
                    .build();

            when(vnPayService.verifyCallback(callback)).thenReturn(true);
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.PENDING)))
                    .thenReturn(Optional.of(testPayment));
            when(vnPayService.isPaymentSuccess(callback)).thenReturn(true);
            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
            when(bookingRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(qrCodeService.generateFullBookingQRCode(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("base64QRCode");
            doNothing().when(emailService).sendBookingConfirmation(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
            doNothing().when(kafkaProducerService).publishPaymentCompleted(any(Payment.class), any(Booking.class));

            // When
            String result = paymentService.handleVNPayIPN(callback);

            // Then
            assertThat(result).contains("RspCode\":\"00");
            verify(kafkaProducerService).publishPaymentCompleted(any(Payment.class), any(Booking.class));
        }

        @Test
        @DisplayName("Should reject invalid signature")
        void handleVNPayIPN_InvalidSignature_RejectsRequest() {
            // Given
            VNPayCallbackRequest callback = VNPayCallbackRequest.builder()
                    .vnp_TxnRef("BK2401011200ABCD")
                    .build();
            when(vnPayService.verifyCallback(callback)).thenReturn(false);

            // When
            String result = paymentService.handleVNPayIPN(callback);

            // Then
            assertThat(result).contains("RspCode\":\"97");
        }

        @Test
        @DisplayName("Should handle booking not found")
        void handleVNPayIPN_BookingNotFound_ReturnsError() {
            // Given
            VNPayCallbackRequest callback = VNPayCallbackRequest.builder()
                    .vnp_TxnRef("INVALID")
                    .build();
            when(vnPayService.verifyCallback(callback)).thenReturn(true);
            when(bookingRepository.findByBookingCode("INVALID")).thenReturn(Optional.empty());

            // When
            String result = paymentService.handleVNPayIPN(callback);

            // Then
            assertThat(result).contains("RspCode\":\"01");
        }
    }

    @Nested
    @DisplayName("VNPay Return Tests")
    class VNPayReturnTests {

        @Test
        @DisplayName("Should handle successful VNPay return")
        void handleVNPayReturn_Success() {
            // Given
            VNPayCallbackRequest callback = VNPayCallbackRequest.builder()
                    .vnp_TxnRef("BK2401011200ABCD")
                    .vnp_ResponseCode("00")
                    .vnp_TransactionNo("TXN123")
                    .build();

            when(vnPayService.verifyCallback(callback)).thenReturn(true);
            when(vnPayService.isPaymentSuccess(callback)).thenReturn(true);
            when(vnPayService.getResponseMessage("00")).thenReturn("Success");
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));

            // When
            PaymentResultResponse result = paymentService.handleVNPayReturn(callback);

            // Then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getBookingCode()).isEqualTo("BK2401011200ABCD");
        }

        @Test
        @DisplayName("Should handle failed VNPay return")
        void handleVNPayReturn_Failed() {
            // Given
            VNPayCallbackRequest callback = VNPayCallbackRequest.builder()
                    .vnp_TxnRef("BK2401011200ABCD")
                    .vnp_ResponseCode("99")
                    .build();

            when(vnPayService.verifyCallback(callback)).thenReturn(true);
            when(vnPayService.isPaymentSuccess(callback)).thenReturn(false);
            when(vnPayService.getResponseMessage("99")).thenReturn("Payment failed");
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));

            // When
            PaymentResultResponse result = paymentService.handleVNPayReturn(callback);

            // Then
            assertThat(result.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("Get Payment Status Tests")
    class GetPaymentStatusTests {

        @Test
        @DisplayName("Should get payment status successfully")
        void getPaymentStatus_Success() {
            // Given
            testPayment.setStatus(PaymentStatus.COMPLETED);
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.COMPLETED)))
                    .thenReturn(Optional.of(testPayment));

            // When
            PaymentStatusResponse result = paymentService.getPaymentStatus("BK2401011200ABCD");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBookingCode()).isEqualTo("BK2401011200ABCD");
            assertThat(result.getPaymentStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("Should return NO_PAYMENT when no payment exists")
        void getPaymentStatus_NoPayment_ReturnsNoPayment() {
            // Given
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.COMPLETED)))
                    .thenReturn(Optional.empty());
            when(paymentRepository.findByBookingIdAndStatus(anyLong(), eq(PaymentStatus.PENDING)))
                    .thenReturn(Optional.empty());

            // When
            PaymentStatusResponse result = paymentService.getPaymentStatus("BK2401011200ABCD");

            // Then
            assertThat(result.getPaymentStatus()).isEqualTo("NO_PAYMENT");
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void getPaymentStatus_BookingNotFound_ThrowsException() {
            // Given
            when(bookingRepository.findByBookingCode("INVALID")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> paymentService.getPaymentStatus("INVALID"))
                    .isInstanceOf(BusinessException.class);
        }
    }
}

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
import com.cinema.cinema.entity.Cinema;
import com.cinema.cinema.entity.Hall;
import com.cinema.cinema.entity.Seat;
import com.cinema.movie.entity.Movie;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.service.KafkaProducerService;
import com.cinema.shared.service.QRCodeService;
import com.cinema.shared.service.RedisLockService;
import com.cinema.show.entity.Show;
import com.cinema.show.entity.Show.ShowStatus;
import com.cinema.show.entity.ShowSeat;
import com.cinema.show.entity.ShowSeat.ShowSeatStatus;
import com.cinema.show.repository.ShowRepository;
import com.cinema.show.repository.ShowSeatRepository;
import com.cinema.user.entity.User;
import com.cinema.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingSeatRepository bookingSeatRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ShowRepository showRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisLockService redisLockService;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Show testShow;
    private ShowSeat testShowSeat;
    private Booking testBooking;
    private Cinema testCinema;
    private Hall testHall;
    private Seat testSeat;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .points(1000)
                .build();

        testCinema = Cinema.builder()
                .id(1L)
                .name("Test Cinema")
                .build();

        testHall = Hall.builder()
                .id(1L)
                .name("Hall 1")
                .cinema(testCinema)
                .build();

        testSeat = Seat.builder()
                .id(1L)
                .rowName("A")
                .seatNumber(1)
                .hall(testHall)
                .build();

        testMovie = Movie.builder()
                .id(1L)
                .title("Test Movie")
                .duration(120)
                .build();

        testShow = Show.builder()
                .id(1L)
                .movie(testMovie)
                .hall(testHall)
                .showDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .status(ShowStatus.SCHEDULED)
                .build();

        testShowSeat = ShowSeat.builder()
                .id(1L)
                .show(testShow)
                .seat(testSeat)
                .price(new BigDecimal("100000"))
                .status(ShowSeatStatus.AVAILABLE)
                .build();

        testBooking = Booking.builder()
                .id(1L)
                .bookingCode("BK2401011200ABCD")
                .user(testUser)
                .show(testShow)
                .totalAmount(new BigDecimal("100000"))
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(new BigDecimal("100000"))
                .status(BookingStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .bookingSeats(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Lock Seats Tests")
    class LockSeatsTests {

        @Test
        @DisplayName("Should lock seats successfully")
        void lockSeats_Success() {
            // Given
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(1L)
                    .showSeatIds(List.of(1L))
                    .build();

            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(bookingRepository.existsPendingBooking(anyLong(), anyLong())).thenReturn(false);
            when(redisLockService.tryLockSeats(anyList(), anyLong(), anyLong())).thenReturn(true);
            when(showSeatRepository.findAllById(anyList())).thenReturn(List.of(testShowSeat));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
                Booking booking = invocation.getArgument(0);
                booking.setId(1L);
                return booking;
            });
            doNothing().when(kafkaProducerService).publishBookingCreated(any(Booking.class));

            // When
            BookingResponse response = bookingService.lockSeats(testUser, request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getBookingCode()).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
            verify(kafkaProducerService).publishBookingCreated(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when exceeding max seats")
        void lockSeats_ExceedsMaxSeats_ThrowsException() {
            // Given
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(1L)
                    .showSeatIds(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L))
                    .build();

            // When & Then
            assertThatThrownBy(() -> bookingService.lockSeats(testUser, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when show not found")
        void lockSeats_ShowNotFound_ThrowsException() {
            // Given
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(999L)
                    .showSeatIds(List.of(1L))
                    .build();
            when(showRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookingService.lockSeats(testUser, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when show is cancelled")
        void lockSeats_ShowCancelled_ThrowsException() {
            // Given
            testShow.setStatus(ShowStatus.CANCELLED);
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(1L)
                    .showSeatIds(List.of(1L))
                    .build();
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));

            // When & Then
            assertThatThrownBy(() -> bookingService.lockSeats(testUser, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when user already has pending booking")
        void lockSeats_PendingBookingExists_ThrowsException() {
            // Given
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(1L)
                    .showSeatIds(List.of(1L))
                    .build();
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(bookingRepository.existsPendingBooking(anyLong(), anyLong())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> bookingService.lockSeats(testUser, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when seats already locked")
        void lockSeats_SeatsAlreadyLocked_ThrowsException() {
            // Given
            LockSeatsRequest request = LockSeatsRequest.builder()
                    .showId(1L)
                    .showSeatIds(List.of(1L))
                    .build();
            when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
            when(bookingRepository.existsPendingBooking(anyLong(), anyLong())).thenReturn(false);
            when(redisLockService.tryLockSeats(anyList(), anyLong(), anyLong())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> bookingService.lockSeats(testUser, request))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Checkout Tests")
    class CheckoutTests {

        @Test
        @DisplayName("Should checkout successfully")
        void checkout_Success() {
            // Given
            CheckoutRequest request = CheckoutRequest.builder()
                    .paymentMethod(PaymentMethod.VNPAY)
                    .pointsToUse(0)
                    .build();
            when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            // When
            PaymentResponse response = bookingService.checkout(1L, 1L, request);

            // Then
            assertThat(response).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void checkout_BookingNotFound_ThrowsException() {
            // Given
            CheckoutRequest request = CheckoutRequest.builder()
                    .paymentMethod(PaymentMethod.VNPAY)
                    .pointsToUse(0)
                    .build();
            when(bookingRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookingService.checkout(1L, 999L, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when booking not pending")
        void checkout_BookingNotPending_ThrowsException() {
            // Given
            testBooking.setStatus(BookingStatus.CONFIRMED);
            CheckoutRequest request = CheckoutRequest.builder()
                    .paymentMethod(PaymentMethod.VNPAY)
                    .pointsToUse(0)
                    .build();
            when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testBooking));

            // When & Then
            assertThatThrownBy(() -> bookingService.checkout(1L, 1L, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when booking expired")
        void checkout_BookingExpired_ThrowsException() {
            // Given
            testBooking.setExpiresAt(LocalDateTime.now().minusMinutes(1));
            CheckoutRequest request = CheckoutRequest.builder()
                    .paymentMethod(PaymentMethod.VNPAY)
                    .pointsToUse(0)
                    .build();
            when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testBooking));

            // When & Then
            assertThatThrownBy(() -> bookingService.checkout(1L, 1L, request))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Cancel Booking Tests")
    class CancelBookingTests {

        @Test
        @DisplayName("Should cancel booking successfully")
        void cancelBooking_Success() {
            // Given
            BookingSeat bookingSeat = BookingSeat.builder()
                    .id(1L)
                    .showSeat(testShowSeat)
                    .price(new BigDecimal("100000"))
                    .build();
            testBooking.getBookingSeats().add(bookingSeat);
            bookingSeat.setBooking(testBooking);

            when(bookingRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            doNothing().when(redisLockService).unlockSeats(anyList(), anyLong());
            doNothing().when(kafkaProducerService).publishBookingCancelled(any(Booking.class));

            // When
            BookingResponse response = bookingService.cancelBooking(1L, 1L);

            // Then
            assertThat(response).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
            verify(kafkaProducerService).publishBookingCancelled(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void cancelBooking_NotFound_ThrowsException() {
            // Given
            when(bookingRepository.findByIdWithSeats(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookingService.cancelBooking(1L, 999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should throw exception when booking not pending")
        void cancelBooking_NotPending_ThrowsException() {
            // Given
            testBooking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testBooking));

            // When & Then
            assertThatThrownBy(() -> bookingService.cancelBooking(1L, 1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Get Booking Tests")
    class GetBookingTests {

        @Test
        @DisplayName("Should get booking by ID successfully")
        void getBookingById_Success() {
            // Given
            when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testBooking));

            // When
            BookingResponse response = bookingService.getBookingById(1L, 1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getBookingCode()).isEqualTo("BK2401011200ABCD");
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void getBookingById_NotFound_ThrowsException() {
            // Given
            when(bookingRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookingService.getBookingById(1L, 999L))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("Should get booking by code successfully")
        void getBookingByCode_Success() {
            // Given
            when(bookingRepository.findByBookingCode("BK2401011200ABCD")).thenReturn(Optional.of(testBooking));

            // When
            BookingResponse response = bookingService.getBookingByCode("BK2401011200ABCD");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getBookingCode()).isEqualTo("BK2401011200ABCD");
        }

        @Test
        @DisplayName("Should get user bookings with pagination")
        void getUserBookings_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Booking> bookingPage = new PageImpl<>(List.of(testBooking));
            when(bookingRepository.findByUserIdWithDetails(1L, pageable)).thenReturn(bookingPage);

            // When
            Page<BookingResponse> result = bookingService.getUserBookings(1L, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Confirm Payment Tests")
    class ConfirmPaymentTests {

        @Test
        @DisplayName("Should confirm payment successfully")
        void confirmPayment_Success() {
            // Given
            Payment payment = Payment.builder()
                    .id(1L)
                    .booking(testBooking)
                    .amount(new BigDecimal("100000"))
                    .paymentMethod(PaymentMethod.VNPAY)
                    .status(PaymentStatus.PENDING)
                    .build();
            testBooking.getPayments().add(payment);

            when(bookingRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(qrCodeService.generateFullBookingQRCode(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("base64QRCode");
            doNothing().when(kafkaProducerService).publishBookingConfirmed(any(Booking.class));

            // When
            BookingResponse response = bookingService.confirmPayment(1L, "TXN123456");

            // Then
            assertThat(response).isNotNull();
            verify(kafkaProducerService).publishBookingConfirmed(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void confirmPayment_BookingNotFound_ThrowsException() {
            // Given
            when(bookingRepository.findByIdWithSeats(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookingService.confirmPayment(999L, "TXN123456"))
                    .isInstanceOf(BusinessException.class);
        }
    }
}

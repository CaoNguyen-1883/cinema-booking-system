package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Booking.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.show s " +
           "LEFT JOIN FETCH s.movie " +
           "LEFT JOIN FETCH s.hall h " +
           "LEFT JOIN FETCH h.cinema " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.bookingSeats bs " +
           "LEFT JOIN FETCH bs.showSeat ss " +
           "LEFT JOIN FETCH ss.seat " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithSeats(@Param("id") Long id);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.show s " +
           "LEFT JOIN FETCH s.movie " +
           "LEFT JOIN FETCH s.hall h " +
           "LEFT JOIN FETCH h.cinema " +
           "WHERE b.user.id = :userId " +
           "ORDER BY b.createdAt DESC")
    Page<Booking> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.user.id = :userId AND b.status = :status " +
           "ORDER BY b.createdAt DESC")
    Page<Booking> findByUserIdAndStatus(@Param("userId") Long userId,
                                        @Param("status") BookingStatus status,
                                        Pageable pageable);

    // Find expired pending bookings
    @Query("SELECT b FROM Booking b " +
           "WHERE b.status = 'PENDING' AND b.expiresAt < :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);

    // Check if user already has pending booking for same show
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.user.id = :userId AND b.show.id = :showId AND b.status = 'PENDING'")
    boolean existsPendingBooking(@Param("userId") Long userId, @Param("showId") Long showId);

    // Count bookings by status
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    long countByStatus(@Param("status") BookingStatus status);

    // Find bookings by show
    @Query("SELECT b FROM Booking b WHERE b.show.id = :showId AND b.status IN :statuses")
    List<Booking> findByShowIdAndStatuses(@Param("showId") Long showId,
                                          @Param("statuses") List<BookingStatus> statuses);
}

package com.cinema.booking.repository;

import com.cinema.booking.entity.Payment;
import com.cinema.booking.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p WHERE p.booking.id = :bookingId AND p.status = :status")
    Optional<Payment> findByBookingIdAndStatus(@Param("bookingId") Long bookingId,
                                               @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p " +
           "JOIN FETCH p.booking b " +
           "WHERE p.transactionId = :transactionId")
    Optional<Payment> findByTransactionIdWithBooking(@Param("transactionId") String transactionId);
}

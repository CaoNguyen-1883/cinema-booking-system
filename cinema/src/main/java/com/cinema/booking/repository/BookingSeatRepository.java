package com.cinema.booking.repository;

import com.cinema.booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    @Query("SELECT bs FROM BookingSeat bs " +
           "JOIN FETCH bs.showSeat ss " +
           "JOIN FETCH ss.seat " +
           "WHERE bs.booking.id = :bookingId")
    List<BookingSeat> findByBookingIdWithDetails(@Param("bookingId") Long bookingId);

    void deleteByBookingId(Long bookingId);
}

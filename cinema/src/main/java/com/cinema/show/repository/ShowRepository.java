package com.cinema.show.repository;

import com.cinema.show.entity.Show;
import com.cinema.show.entity.Show.ShowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.hall h " +
           "JOIN FETCH h.cinema c " +
           "WHERE s.id = :id")
    Optional<Show> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.hall h " +
           "JOIN FETCH h.cinema c " +
           "LEFT JOIN FETCH s.showSeats " +
           "WHERE s.id = :id")
    Optional<Show> findByIdWithSeats(@Param("id") Long id);

    // Find shows by movie
    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.hall h " +
           "JOIN FETCH h.cinema c " +
           "WHERE s.movie.id = :movieId " +
           "AND s.showDate >= :startDate " +
           "AND s.showDate <= :endDate " +
           "AND s.status = :status " +
           "ORDER BY s.showDate, s.startTime")
    List<Show> findByMovieIdAndDateRange(
            @Param("movieId") Long movieId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ShowStatus status);

    // Find shows by cinema
    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.hall h " +
           "WHERE h.cinema.id = :cinemaId " +
           "AND s.showDate = :showDate " +
           "AND s.status = :status " +
           "ORDER BY m.title, s.startTime")
    List<Show> findByCinemaIdAndDate(
            @Param("cinemaId") Long cinemaId,
            @Param("showDate") LocalDate showDate,
            @Param("status") ShowStatus status);

    // Find shows by hall and date (for schedule conflict checking)
    @Query("SELECT s FROM Show s " +
           "WHERE s.hall.id = :hallId " +
           "AND s.showDate = :showDate " +
           "AND s.status != 'CANCELLED'")
    List<Show> findByHallIdAndDate(
            @Param("hallId") Long hallId,
            @Param("showDate") LocalDate showDate);

    // Check for schedule conflict
    @Query("SELECT COUNT(s) > 0 FROM Show s " +
           "WHERE s.hall.id = :hallId " +
           "AND s.showDate = :showDate " +
           "AND s.status != 'CANCELLED' " +
           "AND s.id != :excludeId " +
           "AND ((s.startTime <= :startTime AND s.endTime > :startTime) " +
           "OR (s.startTime < :endTime AND s.endTime >= :endTime) " +
           "OR (s.startTime >= :startTime AND s.endTime <= :endTime))")
    boolean hasScheduleConflict(
            @Param("hallId") Long hallId,
            @Param("showDate") LocalDate showDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    // Find all shows with pagination
    Page<Show> findByStatus(ShowStatus status, Pageable pageable);

    // Find upcoming shows for a movie
    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.hall h " +
           "JOIN FETCH h.cinema c " +
           "WHERE s.movie.id = :movieId " +
           "AND (s.showDate > :today OR (s.showDate = :today AND s.startTime > :now)) " +
           "AND s.status = 'SCHEDULED' " +
           "ORDER BY s.showDate, s.startTime")
    List<Show> findUpcomingShowsByMovieId(
            @Param("movieId") Long movieId,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);

    // Count shows by movie
    long countByMovieIdAndStatus(Long movieId, ShowStatus status);

    // Find shows to complete (past shows that are still SCHEDULED)
    @Query("SELECT s FROM Show s " +
           "WHERE s.status = 'SCHEDULED' " +
           "AND (s.showDate < :today OR (s.showDate = :today AND s.endTime < :now))")
    List<Show> findShowsToComplete(
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);
}

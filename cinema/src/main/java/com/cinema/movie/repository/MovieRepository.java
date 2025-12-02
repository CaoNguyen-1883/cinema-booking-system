package com.cinema.movie.repository;

import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.Movie.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    List<Movie> findByStatusOrderByReleaseDateDesc(MovieStatus status);

    @Query("SELECT m FROM Movie m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.director) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") Integer genreId, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.releaseDate DESC")
    List<Movie> findTopByStatus(@Param("status") MovieStatus status, Pageable pageable);

    boolean existsByTitleAndReleaseDate(String title, java.time.LocalDate releaseDate);
}

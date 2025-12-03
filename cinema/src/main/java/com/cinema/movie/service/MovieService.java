package com.cinema.movie.service;

import com.cinema.movie.dto.*;
import com.cinema.movie.entity.Genre;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.Movie.MovieRating;
import com.cinema.movie.entity.Movie.MovieStatus;
import com.cinema.movie.repository.GenreRepository;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.shared.config.RedisConfig;
import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Cacheable(value = RedisConfig.CACHE_MOVIE_DETAIL, key = "#id")
    public MovieResponse getMovieById(Long id) {
        log.debug("Fetching movie {} from database", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));
        return MovieResponse.fromEntity(movie);
    }

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(MovieResponse::fromEntity);
    }

    public Page<MovieResponse> getMoviesByStatus(String status, Pageable pageable) {
        MovieStatus movieStatus = MovieStatus.valueOf(status.toUpperCase());
        return movieRepository.findByStatus(movieStatus, pageable)
                .map(MovieResponse::fromEntity);
    }

    @Cacheable(value = RedisConfig.CACHE_MOVIES, key = "'now_showing'")
    public List<MovieResponse> getNowShowingMovies() {
        log.debug("Fetching now showing movies from database");
        return movieRepository.findByStatusOrderByReleaseDateDesc(MovieStatus.NOW_SHOWING)
                .stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = RedisConfig.CACHE_MOVIES, key = "'coming_soon'")
    public List<MovieResponse> getComingSoonMovies() {
        log.debug("Fetching coming soon movies from database");
        return movieRepository.findByStatusOrderByReleaseDateDesc(MovieStatus.COMING_SOON)
                .stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<MovieResponse> searchMovies(String keyword, Pageable pageable) {
        return movieRepository.searchByKeyword(keyword, pageable)
                .map(MovieResponse::fromEntity);
    }

    public Page<MovieResponse> getMoviesByGenre(Integer genreId, Pageable pageable) {
        return movieRepository.findByGenreId(genreId, pageable)
                .map(MovieResponse::fromEntity);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_MOVIES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_MOVIE_DETAIL, allEntries = true)
    })
    public MovieResponse createMovie(CreateMovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .originalTitle(request.getOriginalTitle())
                .director(request.getDirector())
                .castMembers(request.getCastMembers())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .endDate(request.getEndDate())
                .rating(MovieRating.valueOf(request.getRating()))
                .language(request.getLanguage() != null ? request.getLanguage() : "Vietnamese")
                .subtitle(request.getSubtitle())
                .posterUrl(request.getPosterUrl())
                .bannerUrl(request.getBannerUrl())
                .trailerUrl(request.getTrailerUrl())
                .description(request.getDescription())
                .status(request.getStatus() != null ?
                        MovieStatus.valueOf(request.getStatus()) : MovieStatus.COMING_SOON)
                .genres(new HashSet<>())
                .build();

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            movie.setGenres(genres);
        }

        Movie savedMovie = movieRepository.save(movie);
        log.info("Created movie: {} (ID: {})", savedMovie.getTitle(), savedMovie.getId());
        return MovieResponse.fromEntity(savedMovie);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_MOVIES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_MOVIE_DETAIL, key = "#id")
    })
    public MovieResponse updateMovie(Long id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        if (request.getTitle() != null) movie.setTitle(request.getTitle());
        if (request.getOriginalTitle() != null) movie.setOriginalTitle(request.getOriginalTitle());
        if (request.getDirector() != null) movie.setDirector(request.getDirector());
        if (request.getCastMembers() != null) movie.setCastMembers(request.getCastMembers());
        if (request.getDuration() != null) movie.setDuration(request.getDuration());
        if (request.getReleaseDate() != null) movie.setReleaseDate(request.getReleaseDate());
        if (request.getEndDate() != null) movie.setEndDate(request.getEndDate());
        if (request.getRating() != null) movie.setRating(MovieRating.valueOf(request.getRating()));
        if (request.getLanguage() != null) movie.setLanguage(request.getLanguage());
        if (request.getSubtitle() != null) movie.setSubtitle(request.getSubtitle());
        if (request.getPosterUrl() != null) movie.setPosterUrl(request.getPosterUrl());
        if (request.getBannerUrl() != null) movie.setBannerUrl(request.getBannerUrl());
        if (request.getTrailerUrl() != null) movie.setTrailerUrl(request.getTrailerUrl());
        if (request.getDescription() != null) movie.setDescription(request.getDescription());
        if (request.getStatus() != null) movie.setStatus(MovieStatus.valueOf(request.getStatus()));

        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            movie.setGenres(genres);
        }

        Movie updatedMovie = movieRepository.save(movie);
        log.info("Updated movie: {} (ID: {})", updatedMovie.getTitle(), updatedMovie.getId());
        return MovieResponse.fromEntity(updatedMovie);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_MOVIES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_MOVIE_DETAIL, key = "#id")
    })
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));
        movieRepository.delete(movie);
        log.info("Deleted movie: {} (ID: {})", movie.getTitle(), id);
    }
}

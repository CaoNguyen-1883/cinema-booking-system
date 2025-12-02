package com.cinema.movie.controller;

import com.cinema.movie.dto.GenreResponse;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.service.GenreService;
import com.cinema.movie.service.MovieService;
import com.cinema.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Public movie APIs")
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Get all movies with pagination")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getAllMovies(
            @PageableDefault(size = 20, sort = "releaseDate") Pageable pageable) {
        Page<MovieResponse> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(movie));
    }

    @GetMapping("/now-showing")
    @Operation(summary = "Get movies currently showing")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNowShowingMovies() {
        List<MovieResponse> movies = movieService.getNowShowingMovies();
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/coming-soon")
    @Operation(summary = "Get upcoming movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getComingSoonMovies() {
        List<MovieResponse> movies = movieService.getComingSoonMovies();
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies by keyword")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> searchMovies(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> movies = movieService.searchMovies(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/genre/{genreId}")
    @Operation(summary = "Get movies by genre")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMoviesByGenre(
            @PathVariable Integer genreId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> movies = movieService.getMoviesByGenre(genreId, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get movies by status (NOW_SHOWING, COMING_SOON, ENDED)")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMoviesByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MovieResponse> movies = movieService.getMoviesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies));
    }
}

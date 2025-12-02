package com.cinema.movie.controller;

import com.cinema.movie.dto.*;
import com.cinema.movie.service.GenreService;
import com.cinema.movie.service.MovieService;
import com.cinema.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Movies", description = "Admin movie management APIs")
public class AdminMovieController {

    private final MovieService movieService;
    private final GenreService genreService;

    @PostMapping
    @Operation(summary = "Create a new movie")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(
            @Valid @RequestBody CreateMovieRequest request) {
        MovieResponse movie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movie, "Movie created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movie")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMovieRequest request) {
        MovieResponse movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Movie deleted successfully"));
    }

    @PostMapping("/genres")
    @Operation(summary = "Create a new genre")
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(
            @Valid @RequestBody CreateGenreRequest request) {
        GenreResponse genre = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(genre, "Genre created successfully"));
    }

    @DeleteMapping("/genres/{id}")
    @Operation(summary = "Delete a genre")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success("Genre deleted successfully"));
    }
}

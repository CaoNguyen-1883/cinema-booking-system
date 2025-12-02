package com.cinema.movie.controller;

import com.cinema.movie.dto.GenreResponse;
import com.cinema.movie.service.GenreService;
import com.cinema.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Public genre APIs")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Get all genres")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAllGenres() {
        List<GenreResponse> genres = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success(genres));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable Integer id) {
        GenreResponse genre = genreService.getGenreById(id);
        return ResponseEntity.ok(ApiResponse.success(genre));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get genre by slug")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreBySlug(@PathVariable String slug) {
        GenreResponse genre = genreService.getGenreBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(genre));
    }
}

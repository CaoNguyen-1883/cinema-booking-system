package com.cinema.show.controller;

import com.cinema.shared.dto.ApiResponse;
import com.cinema.show.dto.ShowResponse;
import com.cinema.show.dto.ShowSeatResponse;
import com.cinema.show.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
@Tag(name = "Shows", description = "Public show/showtime APIs")
public class ShowController {

    private final ShowService showService;

    @GetMapping("/{id}")
    @Operation(summary = "Get show by ID")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(@PathVariable Long id) {
        ShowResponse show = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.success(show));
    }

    @GetMapping("/{id}/with-seats")
    @Operation(summary = "Get show by ID with all seats")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowByIdWithSeats(@PathVariable Long id) {
        ShowResponse show = showService.getShowByIdWithSeats(id);
        return ResponseEntity.ok(ApiResponse.success(show));
    }

    @GetMapping("/{id}/seats")
    @Operation(summary = "Get seat map for a show")
    public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getShowSeats(@PathVariable Long id) {
        List<ShowSeatResponse> seats = showService.getShowSeats(id);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @GetMapping("/by-movie/{movieId}")
    @Operation(summary = "Get shows by movie ID with optional date range")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ShowResponse> shows = showService.getShowsByMovie(movieId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/by-cinema/{cinemaId}")
    @Operation(summary = "Get shows by cinema ID for a specific date")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByCinema(
            @PathVariable Long cinemaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShowResponse> shows = showService.getShowsByCinema(cinemaId, date);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/upcoming/{movieId}")
    @Operation(summary = "Get upcoming shows for a movie")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getUpcomingShows(@PathVariable Long movieId) {
        List<ShowResponse> shows = showService.getUpcomingShows(movieId);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }
}

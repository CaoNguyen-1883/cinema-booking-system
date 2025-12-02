package com.cinema.cinema.controller;

import com.cinema.cinema.dto.*;
import com.cinema.cinema.service.CinemaService;
import com.cinema.cinema.service.HallService;
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
@RequestMapping("/admin/cinemas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Cinemas", description = "Admin cinema management APIs")
public class AdminCinemaController {

    private final CinemaService cinemaService;
    private final HallService hallService;

    // Cinema management
    @PostMapping
    @Operation(summary = "Create a new cinema")
    public ResponseEntity<ApiResponse<CinemaResponse>> createCinema(
            @Valid @RequestBody CreateCinemaRequest request) {
        CinemaResponse cinema = cinemaService.createCinema(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cinema, "Cinema created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a cinema")
    public ResponseEntity<ApiResponse<CinemaResponse>> updateCinema(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCinemaRequest request) {
        CinemaResponse cinema = cinemaService.updateCinema(id, request);
        return ResponseEntity.ok(ApiResponse.success(cinema, "Cinema updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cinema")
    public ResponseEntity<ApiResponse<Void>> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return ResponseEntity.ok(ApiResponse.success("Cinema deleted successfully"));
    }

    // Hall management
    @PostMapping("/{cinemaId}/halls")
    @Operation(summary = "Create a new hall in a cinema")
    public ResponseEntity<ApiResponse<HallResponse>> createHall(
            @PathVariable Long cinemaId,
            @Valid @RequestBody CreateHallRequest request) {
        HallResponse hall = hallService.createHall(cinemaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(hall, "Hall created successfully"));
    }

    @PutMapping("/halls/{hallId}")
    @Operation(summary = "Update a hall")
    public ResponseEntity<ApiResponse<HallResponse>> updateHall(
            @PathVariable Long hallId,
            @Valid @RequestBody UpdateHallRequest request) {
        HallResponse hall = hallService.updateHall(hallId, request);
        return ResponseEntity.ok(ApiResponse.success(hall, "Hall updated successfully"));
    }

    @DeleteMapping("/halls/{hallId}")
    @Operation(summary = "Delete a hall")
    public ResponseEntity<ApiResponse<Void>> deleteHall(@PathVariable Long hallId) {
        hallService.deleteHall(hallId);
        return ResponseEntity.ok(ApiResponse.success("Hall deleted successfully"));
    }

    @PostMapping("/halls/{hallId}/regenerate-seats")
    @Operation(summary = "Regenerate all seats for a hall")
    public ResponseEntity<ApiResponse<Void>> regenerateSeats(@PathVariable Long hallId) {
        hallService.regenerateSeats(hallId);
        return ResponseEntity.ok(ApiResponse.success("Seats regenerated successfully"));
    }
}

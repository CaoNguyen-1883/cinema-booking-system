package com.cinema.cinema.controller;

import com.cinema.cinema.dto.CinemaResponse;
import com.cinema.cinema.dto.HallResponse;
import com.cinema.cinema.dto.SeatResponse;
import com.cinema.cinema.service.CinemaService;
import com.cinema.cinema.service.HallService;
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
@RequestMapping("/cinemas")
@RequiredArgsConstructor
@Tag(name = "Cinemas", description = "Public cinema APIs")
public class CinemaController {

    private final CinemaService cinemaService;
    private final HallService hallService;

    @GetMapping
    @Operation(summary = "Get all cinemas with pagination")
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> getAllCinemas(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CinemaResponse> cinemas = cinemaService.getAllCinemas(pageable);
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cinema by ID")
    public ResponseEntity<ApiResponse<CinemaResponse>> getCinemaById(@PathVariable Long id) {
        CinemaResponse cinema = cinemaService.getCinemaById(id);
        return ResponseEntity.ok(ApiResponse.success(cinema));
    }

    @GetMapping("/{id}/with-halls")
    @Operation(summary = "Get cinema by ID with all halls")
    public ResponseEntity<ApiResponse<CinemaResponse>> getCinemaByIdWithHalls(@PathVariable Long id) {
        CinemaResponse cinema = cinemaService.getCinemaByIdWithHalls(id);
        return ResponseEntity.ok(ApiResponse.success(cinema));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active cinemas")
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> getActiveCinemas() {
        List<CinemaResponse> cinemas = cinemaService.getActiveCinemas();
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    @GetMapping("/by-city")
    @Operation(summary = "Get all active cinemas grouped by city")
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> getActiveCinemasGroupedByCity() {
        List<CinemaResponse> cinemas = cinemaService.getActiveCinemasGroupedByCity();
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    @GetMapping("/cities")
    @Operation(summary = "Get all distinct cities with cinemas")
    public ResponseEntity<ApiResponse<List<String>>> getDistinctCities() {
        List<String> cities = cinemaService.getDistinctCities();
        return ResponseEntity.ok(ApiResponse.success(cities));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get cinemas by city")
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> getCinemasByCity(
            @PathVariable String city,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CinemaResponse> cinemas = cinemaService.getCinemasByCity(city, pageable);
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    @GetMapping("/search")
    @Operation(summary = "Search cinemas by keyword")
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> searchCinemas(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CinemaResponse> cinemas = cinemaService.searchCinemas(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get cinemas by status (ACTIVE, INACTIVE, MAINTENANCE)")
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> getCinemasByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CinemaResponse> cinemas = cinemaService.getCinemasByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(cinemas));
    }

    // Hall endpoints
    @GetMapping("/{cinemaId}/halls")
    @Operation(summary = "Get all halls of a cinema")
    public ResponseEntity<ApiResponse<List<HallResponse>>> getHallsByCinemaId(@PathVariable Long cinemaId) {
        List<HallResponse> halls = hallService.getHallsByCinemaId(cinemaId);
        return ResponseEntity.ok(ApiResponse.success(halls));
    }

    @GetMapping("/{cinemaId}/halls/active")
    @Operation(summary = "Get all active halls of a cinema")
    public ResponseEntity<ApiResponse<List<HallResponse>>> getActiveHallsByCinemaId(@PathVariable Long cinemaId) {
        List<HallResponse> halls = hallService.getActiveHallsByCinemaId(cinemaId);
        return ResponseEntity.ok(ApiResponse.success(halls));
    }

    @GetMapping("/halls/{hallId}")
    @Operation(summary = "Get hall by ID")
    public ResponseEntity<ApiResponse<HallResponse>> getHallById(@PathVariable Long hallId) {
        HallResponse hall = hallService.getHallById(hallId);
        return ResponseEntity.ok(ApiResponse.success(hall));
    }

    @GetMapping("/halls/{hallId}/with-seats")
    @Operation(summary = "Get hall by ID with all seats")
    public ResponseEntity<ApiResponse<HallResponse>> getHallByIdWithSeats(@PathVariable Long hallId) {
        HallResponse hall = hallService.getHallByIdWithSeats(hallId);
        return ResponseEntity.ok(ApiResponse.success(hall));
    }

    @GetMapping("/halls/{hallId}/seats")
    @Operation(summary = "Get all seats of a hall")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByHallId(@PathVariable Long hallId) {
        List<SeatResponse> seats = hallService.getSeatsByHallId(hallId);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @GetMapping("/halls/{hallId}/seats/active")
    @Operation(summary = "Get all active seats of a hall")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getActiveSeatsByHallId(@PathVariable Long hallId) {
        List<SeatResponse> seats = hallService.getActiveSeatsByHallId(hallId);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }
}

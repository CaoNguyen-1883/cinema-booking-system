package com.cinema.show.controller;

import com.cinema.shared.dto.ApiResponse;
import com.cinema.show.dto.CreateShowRequest;
import com.cinema.show.dto.ShowResponse;
import com.cinema.show.dto.UpdateShowRequest;
import com.cinema.show.entity.Show.ShowStatus;
import com.cinema.show.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/shows")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Shows", description = "Admin show management APIs")
public class AdminShowController {

    private final ShowService showService;

    @GetMapping
    @Operation(summary = "Get all shows with pagination")
    public ResponseEntity<ApiResponse<Page<ShowResponse>>> getAllShows(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "showDate") Pageable pageable) {
        ShowStatus showStatus = status != null ? ShowStatus.valueOf(status.toUpperCase()) : null;
        Page<ShowResponse> shows = showService.getAllShows(showStatus, pageable);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/by-hall/{hallId}")
    @Operation(summary = "Get shows by hall and date (for schedule view)")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByHallAndDate(
            @PathVariable Long hallId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShowResponse> shows = showService.getShowsByHallAndDate(hallId, date);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @PostMapping
    @Operation(summary = "Create a new show")
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(
            @Valid @RequestBody CreateShowRequest request) {
        ShowResponse show = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(show, "Show created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a show")
    public ResponseEntity<ApiResponse<ShowResponse>> updateShow(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShowRequest request) {
        ShowResponse show = showService.updateShow(id, request);
        return ResponseEntity.ok(ApiResponse.success(show, "Show updated successfully"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a show")
    public ResponseEntity<ApiResponse<Void>> cancelShow(@PathVariable Long id) {
        showService.cancelShow(id);
        return ResponseEntity.ok(ApiResponse.success("Show cancelled successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a show (only if no bookings)")
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.ok(ApiResponse.success("Show deleted successfully"));
    }
}

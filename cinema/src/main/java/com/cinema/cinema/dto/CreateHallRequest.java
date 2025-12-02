package com.cinema.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHallRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Hall type is required")
    private String hallType; // STANDARD, VIP, IMAX, THREE_D, FOUR_DX

    @NotNull(message = "Total rows is required")
    @Min(value = 1, message = "Total rows must be at least 1")
    @Max(value = 50, message = "Total rows cannot exceed 50")
    private Integer totalRows;

    @NotNull(message = "Seats per row is required")
    @Min(value = 1, message = "Seats per row must be at least 1")
    @Max(value = 50, message = "Seats per row cannot exceed 50")
    private Integer seatsPerRow;
}

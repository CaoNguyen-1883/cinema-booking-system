package com.cinema.show.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShowRequest {

    @FutureOrPresent(message = "Show date must be today or in the future")
    private LocalDate showDate;

    private LocalTime startTime;

    @DecimalMin(value = "40000", message = "Base price must be at least 40,000 VND")
    private BigDecimal basePrice;

    private String status;
}

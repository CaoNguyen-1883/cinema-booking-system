package com.cinema.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHallRequest {
    @Size(max = 100)
    private String name;

    private String hallType; // STANDARD, VIP, IMAX, THREE_D, FOUR_DX

    private String status; // ACTIVE, INACTIVE, MAINTENANCE
}

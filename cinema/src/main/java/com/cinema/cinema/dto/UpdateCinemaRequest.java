package com.cinema.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCinemaRequest {
    @Size(max = 255)
    private String name;

    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String district;

    @Size(max = 20)
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 100)
    private String openingHours;

    private String facilities;

    private String status; // ACTIVE, INACTIVE, MAINTENANCE
}

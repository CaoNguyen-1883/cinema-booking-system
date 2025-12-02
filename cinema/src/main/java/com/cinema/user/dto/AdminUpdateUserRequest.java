package com.cinema.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {
    @Size(min = 2, max = 100)
    private String fullName;

    @Size(max = 20)
    private String phoneNumber;

    private String role;  // CUSTOMER, STAFF, ADMIN

    private String status;  // ACTIVE, INACTIVE, BANNED

    private Integer points;
}

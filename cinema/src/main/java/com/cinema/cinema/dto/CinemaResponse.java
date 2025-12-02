package com.cinema.cinema.dto;

import com.cinema.cinema.entity.Cinema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private String facilities;
    private String status;
    private Integer totalHalls;
    private List<HallResponse> halls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CinemaResponse fromEntity(Cinema cinema) {
        return CinemaResponse.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .address(cinema.getAddress())
                .city(cinema.getCity())
                .district(cinema.getDistrict())
                .phoneNumber(cinema.getPhoneNumber())
                .email(cinema.getEmail())
                .openingHours(cinema.getOpeningHours())
                .facilities(cinema.getFacilities())
                .status(cinema.getStatus().name())
                .totalHalls(cinema.getHalls() != null ? cinema.getHalls().size() : 0)
                .createdAt(cinema.getCreatedAt())
                .updatedAt(cinema.getUpdatedAt())
                .build();
    }

    public static CinemaResponse fromEntityWithHalls(Cinema cinema) {
        CinemaResponse response = fromEntity(cinema);
        if (cinema.getHalls() != null) {
            response.setHalls(cinema.getHalls().stream()
                    .map(HallResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return response;
    }
}

package com.cinema.booking.dto;

import com.cinema.booking.entity.Payment.PaymentMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Min(value = 0, message = "Points to use cannot be negative")
    @Max(value = 100000, message = "Points to use cannot exceed 100,000")
    @Builder.Default
    private Integer pointsToUse = 0;
}

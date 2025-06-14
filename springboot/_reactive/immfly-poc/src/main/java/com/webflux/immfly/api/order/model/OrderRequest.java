package com.webflux.immfly.api.order.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        //TODO convert all decimals to float for better memory usage
        List<OrderItem> products,
        @NotBlank(message = "User ID needs to be provided")
        String userId,
        float total,
        @NotBlank(message = "Seat number needs to be provided")
        String seatNumber,
        @NotBlank(message = "Seat letter needs to be provided")
        Character seatLetter) {
    public record OrderItem(
            @NotBlank(message = "Product ID needs to be provided")
            String productId,
            @NotBlank(message = "Quantity needs to be provided")
            Integer quantity,
            @NotBlank(message = "Unit price needs to be provided")
            float price) {}
}

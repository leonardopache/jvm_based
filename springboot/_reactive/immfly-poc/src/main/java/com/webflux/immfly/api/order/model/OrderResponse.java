package com.webflux.immfly.api.order.model;

import com.webflux.immfly.api.product.model.ProductResponse;
import com.webflux.immfly.core.order.model.OrderStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderResponse(
        String id,
        String userId,
        OrderStatus status,
        String seatNumber,
        Character seatLetter,
        List<OrderProductResponse> products,
        float total,
        String paymentCard,
        String paymentStatus,
        float paymentTotal,
        String paymentGateway) {
    @Builder
    public record OrderProductResponse(
            ProductResponse product,
            int quantity) {}
}

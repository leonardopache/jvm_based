package com.webflux.immfly.api.product.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        String id,
        String name,
        BigDecimal price,
        String image,
        int quantity,
        CategoryResponse category) {
    @Builder
    public record CategoryResponse(String id, String name, CategoryResponse parent) {}
}

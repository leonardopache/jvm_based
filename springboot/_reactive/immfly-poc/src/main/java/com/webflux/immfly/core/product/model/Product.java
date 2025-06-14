package com.webflux.immfly.core.product.model;

import com.webflux.immfly.api.product.model.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Table("products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;
    private String image;
    private int quantity;
    @Column("category_id")
    private UUID categoryId;
    @Transient
    private Category category;

    public static ProductResponse fromProduct(Product product) {
        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .category(mapCategory(product.getCategory()))
                .image(product.getImage())
                .quantity(product.getQuantity())
                .build();
    }

    private static ProductResponse.CategoryResponse mapCategory(Category category) {
        if (Objects.isNull(category)) {
            return null;
        }

        return ProductResponse.CategoryResponse.builder()
                .id(category.getId().toString())
                .name(category.getName())
                .parent(mapCategory(category.getParent()))
                .build();
    }
}
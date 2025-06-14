package com.webflux.immfly.core.order.model;

import com.webflux.immfly.core.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("order_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    @Column("order_id")
    private UUID orderId;
    @Column("product_id")
    private UUID productId;
    @Column("quantity")
    private Integer quantity;
    @Transient
    private Product product;
}

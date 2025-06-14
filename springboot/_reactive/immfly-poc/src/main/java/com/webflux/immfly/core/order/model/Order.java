package com.webflux.immfly.core.order.model;

import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderResponse;
import com.webflux.immfly.api.order.model.OrderResponse.OrderProductResponse;
import com.webflux.immfly.core.product.model.Product;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Table("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private UUID id;
    @Column("user_id" )
    private UUID userId;
    private OrderStatus status;
    @NotEmpty
    private String seatNumber;
    @NotEmpty
    private Character seatLetter;
    @Transient
    private List<OrderProduct> orderProducts;
    private BigDecimal total;
    private String paymentCard;
    private String paymentStatus;
    private String paymentDateTime;
    private String paymentTotal;
    private String paymentGateway;


    public static Order toOrder(OrderRequest request) {
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        List<OrderProduct> orderProducts = Optional.ofNullable(request.products()).orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    UUID productId = UUID.fromString(item.productId());
                    BigDecimal itemTotal = BigDecimal.valueOf(item.quantity()).multiply(BigDecimal.valueOf(item.price()));
                    total.getAndAccumulate(itemTotal, BigDecimal::add);
                    return new OrderProduct(null, productId, item.quantity(), null);
                })
                .toList();

        return Order.builder()
                .userId(UUID.fromString(request.userId()))
                .status(OrderStatus.OPENED)
                .seatLetter(request.seatLetter())
                .seatNumber(request.seatNumber())
                .orderProducts(orderProducts)
                .total(total.get())
                .build();
    }


    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId().toString())
                .status(order.getStatus())
                .total(order.getTotal().floatValue())
                .userId(order.getUserId().toString())
                .seatLetter(order.getSeatLetter())
                .seatNumber(order.getSeatNumber())
                .products(order.getOrderProducts().stream()
                        .map(op -> OrderProductResponse.builder()
                                .quantity(op.getQuantity())
                                .product(Product.fromProduct(op.getProduct()))
                                .build())
                        .toList())
                .paymentGateway(order.getPaymentGateway())
                .paymentCard(order.getPaymentCard())
                .paymentTotal(order.getPaymentTotal())
                .paymentStatus(order.getPaymentStatus())
                .paymentDateTime(order.getPaymentDateTime())
                .build();
    }
}

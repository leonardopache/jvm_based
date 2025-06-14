package com.webflux.immfly.core.order.reposistory;

import com.webflux.immfly.core.order.model.OrderProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OrderProductRepository extends ReactiveCrudRepository<OrderProduct, Void> {
        Flux<OrderProduct> findAllByOrderId(UUID orderId);
}

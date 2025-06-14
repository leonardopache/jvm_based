package com.webflux.immfly.core.order.reposistory;

import com.webflux.immfly.core.order.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {}

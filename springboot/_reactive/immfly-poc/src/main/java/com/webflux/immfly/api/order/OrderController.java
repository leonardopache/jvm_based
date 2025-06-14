package com.webflux.immfly.api.order;

import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderResponse;
import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.webflux.immfly.common.ApiConstants.API_PREFIX_V1;

@RestController
@RequestMapping(API_PREFIX_V1 + "/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Flux<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    public Mono<String> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @PutMapping("/{orderId}")
    public Mono<String> updateOrder(@PathVariable String orderId, @RequestBody OrderRequest request) {
        return orderService.updateOrder(orderId, request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @PostMapping("/{orderId}/purchase")
    public Mono<String> purchaseOrder(@PathVariable String orderId, @RequestBody OrderRequest request) {
        return orderService.purchaseOrder(orderId, request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @DeleteMapping("/{orderId}")
    public Mono<String> deleteOrder(@PathVariable String orderId) {
        return orderService.deleteOrder(orderId);
    }
}

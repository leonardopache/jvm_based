package com.webflux.immfly.api.order;

import static com.webflux.immfly.common.ApiConstants.API_PREFIX_V1;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderResponse;
import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(API_PREFIX_V1 + "/orders")
@AllArgsConstructor
@Tag(name = "Order", description = "Group of Operation related to Orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders.")
    public Flux<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order and returns its ID.")
    public Mono<String> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update an existing order", description = "Updates an existing order, the order needs to be in OPENED status, and returns its ID.")
    public Mono<String> updateOrder(@PathVariable String orderId, @RequestBody OrderRequest request) {
        return orderService.updateOrder(orderId, request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @PostMapping("/{orderId}/purchase")
    @Operation(summary = "Purchase an order", description = "Purchases an order, the order needs to be in OPENED status, and returns its ID.")
    public Mono<String> purchaseOrder(@PathVariable String orderId, @RequestBody OrderRequest request) {
        return orderService.purchaseOrder(orderId, request)
                .map(Order::getId)
                .map(UUID::toString);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete an order", description = "Deletes an order by its ID.")
    public Mono<String> deleteOrder(@PathVariable String orderId) {
        return orderService.deleteOrder(orderId);
    }
}

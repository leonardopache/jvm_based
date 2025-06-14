package com.webflux.immfly.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderRequest.OrderItem;
import com.webflux.immfly.api.order.model.OrderResponse;
import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderControllerTest {
    @Mock
    OrderService orderService;
    private WebTestClient webTestClient;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        OrderController orderController = new OrderController(orderService);
        webTestClient = WebTestClient.bindToController(orderController).build();
    }

    @Test
    void createOrder() {
        var orderUUID = UUID.randomUUID();

        var orderRequest = OrderRequest.builder()
                .products(List.of(new OrderItem(UUID.randomUUID().toString(), 1, 1.5F)))
                .userId(UUID.randomUUID().toString())
                .total(10.00F)
                .build();

        when(orderService.createOrder(orderRequest))
                .thenReturn(Mono.just(Order.builder().id(orderUUID).build()));

        webTestClient.post()
                .uri("/api/v1/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectBody(String.class)
                .consumeWith(result -> assertEquals(result.getResponseBody(), orderUUID.toString()));
    }

    @Test
    void updateOrder() {
        var orderUUID = UUID.randomUUID();

        var orderRequest = OrderRequest.builder()
                .products(List.of(new OrderItem(UUID.randomUUID().toString(), 1, 1.5F)))
                .userId(UUID.randomUUID().toString())
                .total(10.00F)
                .build();

        when(orderService.updateOrder(orderUUID.toString(), orderRequest))
                .thenReturn(Mono.just(Order.builder().id(orderUUID).build()));

        webTestClient.put()
                .uri("/api/v1/orders/" + orderUUID)
                .bodyValue(orderRequest)
                .exchange()
                .expectBody(String.class)
                .consumeWith(result -> assertEquals(result.getResponseBody(), orderUUID.toString()));
    }

    @Test
    void deleteOrder() {
        var orderUUID = UUID.randomUUID().toString();

        when(orderService.deleteOrder(orderUUID))
                .thenReturn(Mono.just(orderUUID));

        webTestClient.delete()
                .uri("/api/v1/orders/" + orderUUID)
                .exchange()
                .expectBody(String.class)
                .consumeWith(result -> assertEquals(result.getResponseBody(), orderUUID));
    }

    @Test
    void purchaseOrder() {
        var orderUUID = UUID.randomUUID();

        var orderRequest = OrderRequest.builder()
                .products(List.of(new OrderItem(UUID.randomUUID().toString(), 1, 1.5F)))
                .userId(UUID.randomUUID().toString())
                .total(10.00F)
                .build();

        when(orderService.purchaseOrder(orderUUID.toString(), orderRequest))
                .thenReturn(Mono.just(Order.builder().id(orderUUID).build()));

        webTestClient.post()
                .uri("/api/v1/orders/" + orderUUID + "/purchase")
                .bodyValue(orderRequest)
                .exchange()
                .expectBody(String.class)
                .consumeWith(result -> assertEquals(result.getResponseBody(), orderUUID.toString()));
    }

    @Test
    void getOrders() {
        var orderResponse = OrderResponse.builder().id(UUID.randomUUID().toString()).build();
        when(orderService.getOrders())
                .thenReturn(Flux.just(orderResponse));

        webTestClient.get()
                .uri("/api/v1/orders")
                .exchange()
                .expectStatus().isOk();
    }
}
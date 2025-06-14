package com.webflux.immfly.core.order;

import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderRequest.OrderItem;
import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.model.OrderStatus;
import com.webflux.immfly.core.order.reposistory.OrderRepository;
import com.webflux.immfly.core.order.service.OrderProductService;
import com.webflux.immfly.core.order.service.OrderService;
import com.webflux.immfly.core.product.model.Product;
import com.webflux.immfly.core.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderProductService orderProductService;
    @Mock
    ProductService productService;
    @InjectMocks
    OrderService orderService;

    private static Stream<Arguments> createOrder() {
        return Stream.of(
                Arguments.of("7002d4f1-e333-4955-9150-fd8d95b1e5ae",
                        OrderRequest.builder()
                                .userId(UUID.randomUUID().toString())
                                .seatNumber("12")
                                .seatLetter('A')
                                .build()),
                Arguments.of("7002d4f1-e333-4955-9150-fd8d95b1e5ae",
                        OrderRequest.builder()
                                .userId(UUID.randomUUID().toString())
                                .products(List.of(new OrderRequest.OrderItem("7002d4f1-e333-4955-9150-fd8d95b1e5ae", 1, 1.5F)))
                                .build()));
    }

    @ParameterizedTest
    @MethodSource
    void createOrder(String productId, OrderRequest request) {
        var orderId = UUID.randomUUID();

        var expectedProduct = Product.builder().id(UUID.fromString(productId)).quantity(10).build();
        when(productService.getProductById(UUID.fromString(productId)))
                .thenReturn(Mono.just(expectedProduct));

        var orderSaved = Order.toOrder(request);
        orderSaved.setId(orderId);
        when(orderRepository.save(Order.toOrder(request)))
                .thenReturn(Mono.just(orderSaved));

        when(orderProductService.saveProducts(orderSaved.getOrderProducts()))
                .thenReturn(Flux.fromIterable(orderSaved.getOrderProducts()));

        StepVerifier.create(orderService.createOrder(request))
                .expectNext(orderSaved)
                .verifyComplete();
    }

    @Test
    void createOrderWithProductsNotAvailable() {
        var orderId = UUID.randomUUID();
        var productId = UUID.randomUUID();
        var request = OrderRequest.builder()
                .userId(UUID.randomUUID().toString())
                .products(List.of(new OrderRequest.OrderItem(productId.toString(), 10, 1.5F)))
                .build();

        var expectedProduct = Product.builder().id(productId).quantity(1).build();
        when(productService.getProductById(productId))
                .thenReturn(Mono.just(expectedProduct));

        var expectedOrder = Order.builder().id(orderId).build();
        when(orderRepository.save(Order.toOrder(request)))
                .thenReturn(Mono.just(expectedOrder));

        StepVerifier.create(orderService.createOrder(request))
                .expectErrorMatches(error -> error.getMessage().equals("Product with ID: [" + productId + "] is out of stock"))
                .verify();
    }

    @Test
    void updateOrder() {
        var orderId = UUID.randomUUID();
        var productIdA = UUID.randomUUID();
        var orderItemA = new OrderItem(productIdA.toString(), 1, 1.5F);
        var productIdB = UUID.randomUUID();
        var orderItemB = new OrderItem(productIdB.toString(), 2, 1.5F);
        var productIdC = UUID.randomUUID();
        var orderItemC = new OrderItem(productIdC.toString(), 3, 1.5F);
        var request = OrderRequest.builder()
                .userId(UUID.randomUUID().toString())
                .products(List.of(orderItemA, orderItemB, orderItemC))
                .seatLetter('A')
                .seatNumber("12")
                .build();

        var expectedOrder = Order.builder()
                .id(orderId)
                .seatLetter('A')
                .seatNumber("12")
                .status(OrderStatus.OPENED)
                .build();
        when(orderRepository.findById(orderId))
                .thenReturn(Mono.just(expectedOrder));

        var expectedProduct = Product.builder().quantity(10).build();
        when(productService.getProductById(any()))
                .thenReturn(Mono.just(expectedProduct));

        var orderToUpdate = Order.toOrder(request);
        orderToUpdate.setId(orderId);
        when(orderRepository.save(orderToUpdate))
                .thenReturn(Mono.just(orderToUpdate));

        when(orderProductService.deleteOrderProducts(orderToUpdate.getId()))
                .thenReturn(Mono.empty());

        when(orderProductService.saveProducts(orderToUpdate.getOrderProducts()))
                .thenReturn(Flux.fromIterable(orderToUpdate.getOrderProducts()));

        var expectedOrderUpdated = Order.toOrder(request);
        expectedOrderUpdated.setId(orderId);
        expectedOrderUpdated.setUserId(UUID.fromString(request.userId()));
        expectedOrderUpdated.setStatus(OrderStatus.OPENED);
        expectedOrderUpdated.getOrderProducts().forEach(orderProduct -> orderProduct.setOrderId(orderId));
        StepVerifier.create(orderService.updateOrder(orderId.toString(), request))
                .expectNext(expectedOrderUpdated)
                .verifyComplete();
    }

    @Test
    void updateOrderNotFound() {
        var orderId = UUID.randomUUID();
        var request = OrderRequest.builder()
                .userId(UUID.randomUUID().toString())
                .build();
        when(orderRepository.findById(orderId))
                .thenReturn(Mono.empty());

        var orderToUpdate = Order.toOrder(request);
        orderToUpdate.setId(orderId);
        when(orderRepository.save(orderToUpdate))
                .thenReturn(Mono.just(Order.builder().id(orderId).build()));

        StepVerifier.create(orderService.updateOrder(orderId.toString(), request))
                .expectError()
                .verify();
    }

    @Test
    void purchaseOrder() {
        var orderId = UUID.randomUUID();
        var productIdA = UUID.randomUUID();
        var orderItemA = new OrderItem(productIdA.toString(), 1, 1.5F);
        var productIdB = UUID.randomUUID();
        var orderItemB = new OrderItem(productIdB.toString(), 2, 1.5F);
        var productIdC = UUID.randomUUID();
        var orderItemC = new OrderItem(productIdC.toString(), 3, 1.5F);
        var request = OrderRequest.builder()
                .userId(UUID.randomUUID().toString())
                .seatLetter('A')
                .seatNumber("12")
                .total(12.00F)
                .products(List.of(orderItemA, orderItemB, orderItemC))
                .total(9.0F)
                .build();

        var expectedOrder = Order.builder()
                .id(orderId)
                .seatLetter('A')
                .seatNumber("12")
                .status(OrderStatus.OPENED)
                .build();
        when(orderRepository.findById(orderId))
                .thenReturn(Mono.just(expectedOrder));

        var expectedProduct = Product.builder().quantity(10).build();
        when(productService.getProductById(any()))
                .thenReturn(Mono.just(expectedProduct));

        var orderToUpdate = Order.toOrder(request);
        orderToUpdate.setId(orderId);
        when(orderRepository.save(orderToUpdate))
                .thenReturn(Mono.just(orderToUpdate));

        when(orderProductService.deleteOrderProducts(orderToUpdate.getId()))
                .thenReturn(Mono.empty());

        when(orderProductService.saveProducts(orderToUpdate.getOrderProducts()))
                .thenReturn(Flux.fromIterable(orderToUpdate.getOrderProducts()));

        var orderUpdated = Order.toOrder(request);
        orderUpdated.setId(orderId);
        orderUpdated.setUserId(UUID.fromString(request.userId()));
        orderUpdated.setStatus(OrderStatus.OPENED);
        orderUpdated.getOrderProducts().forEach(orderProduct -> orderProduct.setOrderId(orderId));
        orderUpdated.setPaymentGateway("not implemented");
        orderUpdated.setPaymentCard("0000-0000-0000-0000");
        orderUpdated.setPaymentTotal(orderUpdated.getTotal());
        orderUpdated.setPaymentStatus("Paid");
        orderUpdated.setStatus(OrderStatus.FINISHED);

        when(orderRepository.save(orderUpdated))
                .thenReturn(Mono.just(orderUpdated));

        StepVerifier.create(orderService.purchaseOrder(orderId.toString(), request))
                .expectNext(orderUpdated)
                .verifyComplete();
    }

    @Test
    void deleteOrder() {
        var orderId = UUID.randomUUID();
        var orderDB = Order.builder()
                .id(orderId)
                .status(OrderStatus.OPENED)
                .build();

        when(orderRepository.findById(orderId))
                .thenReturn(Mono.just(orderDB));

        var orderToUpdate = Order.builder()
                .id(orderId)
                .status(OrderStatus.DROPED)
                .build();
        when(orderRepository.save(orderToUpdate))
                .thenReturn(Mono.just(orderToUpdate));

        StepVerifier.create(orderService.deleteOrder(orderId.toString()))
                .expectNext(orderToUpdate.getId().toString())
                .verifyComplete();
    }

    @Test
    void getOrders() {

        when(orderRepository.findAll())
                .thenReturn(Flux.fromIterable(
                        List.of(
                                Order.builder()
                                        .id(UUID.randomUUID())
                                        .userId(UUID.randomUUID())
                                        .build(),
                                Order.builder()
                                        .id(UUID.randomUUID())
                                        .userId(UUID.randomUUID())
                                        .build())));

        when(orderProductService.getProductsFromOrderProduct(any()))
                .thenReturn(Flux.fromIterable(List.of()));

        StepVerifier.create(orderService.getOrders())
                .expectNextCount(2)
                .verifyComplete();
    }
}
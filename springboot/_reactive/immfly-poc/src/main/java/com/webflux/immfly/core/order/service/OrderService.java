package com.webflux.immfly.core.order.service;

import com.webflux.immfly.api.order.model.OrderRequest;
import com.webflux.immfly.api.order.model.OrderRequest.OrderItem;
import com.webflux.immfly.api.order.model.OrderResponse;
import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.model.OrderStatus;
import com.webflux.immfly.core.order.reposistory.OrderRepository;
import com.webflux.immfly.core.product.service.ProductService;
import com.webflux.immfly.remote.PaymentGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.webflux.immfly.core.product.service.ProductService.PRODUCT_ERROR_MSG_OUT_OF_STOCK;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final OrderProductService orderProductService;
    private final ProductService productService;

    // TODO create proper error handling for order
    private static final String ORDER_ERROR_MSG_NOT_FOUND = "Order with ID: [%s] not found";
    private static final String ORDER_ERROR_MSG_INVALID_STATUS = "Order with ID: [%s] is not opened";

    public Mono<Order> createOrder(OrderRequest request) {
        return Mono.just(request)
                .delayUntil(order -> Mono.defer(() -> checkStock(order.products())))
                .zipWhen(orderRequest -> orderRepository.save(Order.toOrder(orderRequest)), (req, order) -> order)
                .map(order -> {
                    order.getOrderProducts()
                            .forEach(orderProduct -> orderProduct.setOrderId(order.getId()));
                    return order;
                })
                .zipWhen(order -> orderProductService.saveProducts(order.getOrderProducts())
                        .collectList(), (order, orderProducts) -> order);
    }

    // TODO in some way the price should be from product and not what was provided in request
    public Mono<Order> updateOrder(String orderId, OrderRequest request) {
        var order = Order.toOrder(request);
        order.setId(UUID.fromString(orderId));
        return orderRepository.findById(UUID.fromString(orderId))
                .switchIfEmpty(Mono.error(new Throwable(ORDER_ERROR_MSG_NOT_FOUND.formatted(orderId))))
                .flatMap(OrderService::validateOrderStatusOpened)
                .delayUntil(orderDb -> Mono.defer(() -> checkStock(request.products())))
                .then(orderRepository.save(order))
                .zipWhen(updatedOrder ->
                        orderProductService.deleteOrderProducts(updatedOrder.getId()).then(Mono.just(updatedOrder)),
                        (updatedOrder, v) -> updatedOrder)
                .map(orderUpdated -> {
                    orderUpdated.getOrderProducts()
                            .forEach(orderProduct -> orderProduct.setOrderId(orderUpdated.getId()));
                    return orderUpdated;
                })
                .zipWhen(updatedOrder ->
                        orderProductService.saveProducts(updatedOrder.getOrderProducts())
                                .collectList(),
                        (updatedOrder, orderProducts) -> updatedOrder);
    }

    public Mono<String> deleteOrder(String orderId) {
        return orderRepository.findById(UUID.fromString(orderId))
                .switchIfEmpty(Mono.error(new Throwable(ORDER_ERROR_MSG_NOT_FOUND.formatted(orderId))))
                .flatMap(OrderService::validateOrderStatusOpened)
                .map(order -> {
                    order.setStatus(OrderStatus.DROPED);
                    return order;
                })
                .flatMap(orderRepository::save)
                .map(Order::getId)
                .map(UUID::toString);
    }

    private static Mono<Order> validateOrderStatusOpened(Order order) {
        return (order.getStatus() != OrderStatus.OPENED) ?
                Mono.error(new Throwable(ORDER_ERROR_MSG_INVALID_STATUS.formatted(order.getId())))
                : Mono.just(order);
    }

    public Mono<Order> purchaseOrder(String orderId, OrderRequest request) {
        return updateOrder(orderId, request)
                .flatMap(PaymentGateway::pay)
                .flatMap(orderRepository::save);
    }

    public Mono<Void> checkStock(List<OrderItem> products) {
        if (CollectionUtils.isEmpty(products)) {
            return Mono.empty();
        }
        return Flux.fromStream(products.stream())
                .flatMap(item -> productService.getProductById(UUID.fromString(item.productId()))
                        .zipWith(Mono.just(item)))
                .handle((tuple, sink) -> {
                    if (tuple.getT1().getQuantity() < tuple.getT2().quantity()) {
                        sink.error(new RuntimeException(PRODUCT_ERROR_MSG_OUT_OF_STOCK.formatted(tuple.getT2().productId())));
                    }
                })
                .then();
    }

    public Flux<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .flatMap(order -> orderProductService.getProductsFromOrderProduct(order.getId())
                        .collectList()
                        .map(orderProducts -> {
                            order.setOrderProducts(orderProducts);
                            return order;
                        }))
                .map(Order::toOrderResponse);
    }

}

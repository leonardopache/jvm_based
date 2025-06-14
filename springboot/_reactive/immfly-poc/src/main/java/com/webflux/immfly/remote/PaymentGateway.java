package com.webflux.immfly.remote;

import com.webflux.immfly.core.order.model.Order;
import com.webflux.immfly.core.order.model.OrderStatus;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class PaymentGateway {
    public static Mono<Order> pay(Order order) {
        order.setPaymentGateway("not implemented");
        order.setPaymentCard("0000-0000-0000-0000");
        order.setPaymentTotal(order.getTotal());
        order.setPaymentStatus("Paid");
        order.setStatus(OrderStatus.FINISHED);
        return Mono.just(order);
    }
}

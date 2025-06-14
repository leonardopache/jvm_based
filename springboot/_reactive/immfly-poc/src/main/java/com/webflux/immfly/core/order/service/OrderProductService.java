package com.webflux.immfly.core.order.service;

import com.webflux.immfly.core.order.model.OrderProduct;
import com.webflux.immfly.core.order.reposistory.OrderProductRepository;
import com.webflux.immfly.core.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;
    private final ProductService productService;

    public Flux<OrderProduct> getProductsFromOrderProduct(UUID orderId) {
        return orderProductRepository.findAllByOrderId(orderId)
                .flatMap(orderProduct -> productService.getProductById(orderProduct.getProductId())
                        .map(product -> {
                            orderProduct.setProduct(product);
                            return orderProduct;
                        }));
    }

    public Flux<OrderProduct> saveProducts(List<OrderProduct> orderProducts) {
        return orderProductRepository.saveAll(orderProducts);
    }
}

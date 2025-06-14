package com.webflux.immfly.core.product;

import com.webflux.immfly.core.product.model.Product;
import com.webflux.immfly.core.product.repository.ProductRepository;
import com.webflux.immfly.core.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductService productService;

    @Test
    void getProducts() {
        var product1 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 1")
                .price(BigDecimal.valueOf(10.00))
                .build();
        var product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .price(BigDecimal.valueOf(10.00))
                .build();
        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        var expectedResponse = List.of(Product.fromProduct(product1), Product.fromProduct(product2));

        StepVerifier.create(productService.getProducts())
                .expectNextCount(2)
                .expectNextMatches(expectedResponse::contains)
                .expectComplete();
    }

    @Test
    void getProductNotFound() {
        var productId = UUID.randomUUID();
        when(productRepository.findById(productId))
                .thenReturn(Mono.empty());

        StepVerifier.create(productService.getProductById(productId))
                .expectErrorMatches(error -> error.getMessage().equals("Product with ID: [" + productId + "] not found"))
                .verify();
    }
}
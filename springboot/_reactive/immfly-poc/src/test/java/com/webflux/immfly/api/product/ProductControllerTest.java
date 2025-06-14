package com.webflux.immfly.api.product;

import com.webflux.immfly.api.product.model.ProductResponse;
import com.webflux.immfly.core.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductController productController = new ProductController(productService);
        webTestClient = WebTestClient.bindToController(productController).build();
    }

    @Test
    void getProducts() {
        when(productService.getProducts())
                .thenReturn(Flux.just(ProductResponse.builder().build(), ProductResponse.builder().build()));

        webTestClient.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse[].class)
                .consumeWith(result -> assertEquals(2, result.getResponseBody().length));
    }
}
package com.webflux.immfly.core.product.service;

import com.webflux.immfly.api.product.model.ProductResponse;
import com.webflux.immfly.core.product.model.Product;
import com.webflux.immfly.core.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private static final String PRODUCT_ERROR_MSG_NOT_FOUND = "Product with ID: [%s] not found";
    public static final String PRODUCT_ERROR_MSG_OUT_OF_STOCK = "Product with ID: [%s] is out of stock";

    public Flux<ProductResponse> getProducts() {
        return productRepository.findAll()
                .flatMap(this::getCategoryRecursive)
                .map(Product::fromProduct);
    }

    public Mono<Product> getProductById(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new Throwable(PRODUCT_ERROR_MSG_NOT_FOUND.formatted(productId))))
                .flatMap(this::getCategoryRecursive);
    }

    private Mono<Product> getCategoryRecursive(Product product) {
        return categoryService.getCategoryById(product.getCategoryId())
                .map(category -> {
                    product.setCategory(category);
                    return product;
                });
    }
}

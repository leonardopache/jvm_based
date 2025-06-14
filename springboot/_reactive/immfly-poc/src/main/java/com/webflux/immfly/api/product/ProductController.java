package com.webflux.immfly.api.product;

import static com.webflux.immfly.common.ApiConstants.API_PREFIX_V1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webflux.immfly.api.product.model.ProductResponse;
import com.webflux.immfly.core.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(API_PREFIX_V1 + "/products")
@AllArgsConstructor
@Tag(name = "Product", description = "Group of Operation related to Products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a list of all products available.")
    public Flux<ProductResponse> getProducts() {
        return productService.getProducts();
    }
}

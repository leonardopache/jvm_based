package com.webflux.immfly.api.product;

import com.webflux.immfly.api.product.model.ProductResponse;
import com.webflux.immfly.core.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.webflux.immfly.common.ApiConstants.API_PREFIX_V1;

@RestController
@RequestMapping(API_PREFIX_V1 + "/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Flux<ProductResponse> getProducts() {
        return productService.getProducts();
    }
}

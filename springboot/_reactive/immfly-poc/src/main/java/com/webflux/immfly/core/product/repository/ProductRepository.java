package com.webflux.immfly.core.product.repository;

import com.webflux.immfly.core.product.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {}
package com.webflux.immfly.core.product.repository;

import com.webflux.immfly.core.product.model.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface CategoryRepository extends ReactiveCrudRepository<Category, UUID> {
}

package com.webflux.immfly.core.product.service;

import com.webflux.immfly.core.product.model.Category;
import com.webflux.immfly.core.product.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Mono<Category> getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new Throwable("Category with ID: [" + categoryId + "] not found")))
                .flatMap(category -> {
                    if (category.getParentId() != null) {
                        return getCategoryById(category.getParentId())
                                .map(parentCategory -> {
                                    category.setParent(parentCategory);
                                    return category;
                                });
                    }
                    return Mono.just(category);
                });
    }
}

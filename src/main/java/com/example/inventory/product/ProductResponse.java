package com.example.inventory.product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt) {
    static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}

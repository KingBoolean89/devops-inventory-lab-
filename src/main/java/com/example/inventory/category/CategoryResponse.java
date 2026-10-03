package com.example.inventory.category;

import java.time.Instant;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {
    static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}

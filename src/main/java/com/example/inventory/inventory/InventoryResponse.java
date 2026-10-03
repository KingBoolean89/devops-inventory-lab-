package com.example.inventory.inventory;

import java.time.Instant;

public record InventoryResponse(
        Long productId,
        String sku,
        String productName,
        Integer quantity,
        Integer reorderLevel,
        Instant updatedAt) {
    static InventoryResponse from(InventoryItem item) {
        return new InventoryResponse(
                item.getProductId(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getReorderLevel(),
                item.getUpdatedAt());
    }
}

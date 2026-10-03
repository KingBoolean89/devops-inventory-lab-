package com.example.inventory.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
        @NotNull @Min(0) Integer quantity,
        @NotNull @Min(0) Integer reorderLevel) {
}

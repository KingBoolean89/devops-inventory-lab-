package com.example.inventory.inventory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    @Override
    @EntityGraph(attributePaths = "product")
    List<InventoryItem> findAll();

    @Override
    @EntityGraph(attributePaths = "product")
    Optional<InventoryItem> findById(Long productId);
}

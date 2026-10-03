package com.example.inventory.inventory;

import com.example.inventory.common.ResourceNotFoundException;
import com.example.inventory.product.Product;
import com.example.inventory.product.ProductRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryController(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<InventoryResponse> list() {
        return inventoryRepository.findAll().stream().map(InventoryResponse::from).toList();
    }

    @GetMapping("/{productId}")
    public InventoryResponse get(@PathVariable Long productId) {
        return InventoryResponse.from(findInventory(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponse> upsert(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " was not found"));
        boolean exists = inventoryRepository.existsById(productId);
        InventoryItem item = inventoryRepository.findById(productId).orElseGet(InventoryItem::new);
        item.setProduct(product);
        item.setQuantity(request.quantity());
        item.setReorderLevel(request.reorderLevel());
        InventoryItem saved = inventoryRepository.save(item);
        if (exists) {
            return ResponseEntity.ok(InventoryResponse.from(saved));
        }
        return ResponseEntity.created(URI.create("/api/inventory/" + saved.getProductId()))
                .body(InventoryResponse.from(saved));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        InventoryItem item = findInventory(productId);
        inventoryRepository.delete(item);
        return ResponseEntity.noContent().build();
    }

    private InventoryItem findInventory(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product " + productId + " was not found"));
    }
}

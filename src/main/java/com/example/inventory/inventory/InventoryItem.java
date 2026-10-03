package com.example.inventory.inventory;

import com.example.inventory.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getProductId() {
        return productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

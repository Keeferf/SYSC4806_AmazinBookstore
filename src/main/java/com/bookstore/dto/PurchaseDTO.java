package com.bookstore.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PurchaseDTO {
    private Long id;
    private LocalDateTime purchaseDate;
    private List<CartItemDTO> items;

    public PurchaseDTO(Long id, LocalDateTime purchaseDate, List<CartItemDTO> items) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}

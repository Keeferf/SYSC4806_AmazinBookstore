package com.bookstore.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a purchase transaction in the bookstore.
 * Contains information about the purchase ID, date of purchase, and the items purchased.
 */
public class PurchaseDTO {
    private Long id;
    private LocalDateTime purchaseDate;
    private List<CartItemDTO> items;

    /**
     * Constructs a new PurchaseDTO with the specified parameters.
     *
     * @param id           the unique identifier of the purchase
     * @param purchaseDate the date and time when the purchase was made
     * @param items        the list of items included in the purchase
     */
    public PurchaseDTO(Long id, LocalDateTime purchaseDate, List<CartItemDTO> items) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.items = items;
    }

    /**
     * Returns the unique identifier of the purchase.
     *
     * @return the purchase ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the purchase.
     *
     * @param id the purchase ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the date and time when the purchase was made.
     *
     * @return the purchase date and time
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the date and time of the purchase.
     *
     * @param purchaseDate the purchase date and time to set
     */
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Returns the list of items included in the purchase.
     *
     * @return the list of cart items
     */
    public List<CartItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the list of items included in the purchase.
     *
     * @param items the list of cart items to set
     */
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}

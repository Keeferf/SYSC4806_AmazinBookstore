package com.bookstore.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "checkouts")
public class Checkout {
    @Id
    private String id;

    // Store user ID instead of DBRef
    private String userId;

    // Instead of using DBRef, we'll store the items directly
    private List<PurchaseItem> items = new ArrayList<>();

    private LocalDateTime purchaseDate;

    /**
     * Default constructor for Checkout.
     * Sets the purchase date to the current date and time.
     */
    public Checkout() {
        this.purchaseDate = LocalDateTime.now();
    }

    /**
     * Constructs a Checkout with the specified user and initializes a new list of purchased items.
     * Sets the purchase date to the current date and time.
     * @param user the user associated with the checkout
     */
    public Checkout(User user) {
        if (user != null) {
            this.userId = user.getId();
        }
        this.items = new ArrayList<>();
        this.purchaseDate = LocalDateTime.now();
    }

    /**
     * Gets the ID of the checkout.
     * @return the ID of the checkout
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the checkout.
     * @param id the new ID of the checkout
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user ID associated with the checkout.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the checkout.
     * @param userId the new user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the list of purchased items in the checkout.
     * @return the list of purchased items
     */
    public List<PurchaseItem> getItems() {
        return items;
    }

    /**
     * Sets the list of purchased items in the checkout.
     * @param items the new list of purchased items
     */
    public void setItems(List<PurchaseItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Gets the purchase date of the checkout.
     * @return the purchase date of the checkout
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the purchase date of the checkout.
     * @param purchaseDate the new purchase date of the checkout
     */
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Adds an item to the checkout's list of items.
     * @param item the item to add
     */
    public void addItem(PurchaseItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    /**
     * Removes an item from the checkout's list of items.
     * @param item the item to remove
     */
    public void removeItem(PurchaseItem item) {
        if (this.items != null) {
            this.items.remove(item);
        }
    }
}
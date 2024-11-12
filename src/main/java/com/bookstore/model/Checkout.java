package com.bookstore.model;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<PurchaseItem> items;

    private LocalDateTime purchaseDate;

    /**
     * Default constructor for Checkout
     */
    public Checkout() {
        this.purchaseDate = LocalDateTime.now();
    }

    /**
     * Constructs a Checkout with the specified user and list of purchased items.
     * Sets the purchase date to the current date and time.
     * @param user the user associated with the checkout
     * @param items the list of purchased items
     */
    public Checkout(User user, List<PurchaseItem> items) {
        this.user = user;
        this.items = items;
        this.purchaseDate = LocalDateTime.now();
    }

    /**
     * Gets the ID of the checkout.
     * @return the ID of the checkout
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the checkout.
     * @param id the new ID of the checkout
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user associated with the checkout.
     * @return the user associated with the checkout
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with the checkout.
     * @param user the new user associated with the checkout
     */
    public void setUser(User user) {
        this.user = user;
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
        this.items = items;
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
}
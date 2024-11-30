package com.bookstore.model;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "carts")
public class Cart {

    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private List<CartItem> items = new ArrayList<>();

    /**
     * Default constructor for JPA.
     */
    public Cart() {}

    /**
     * Constructs a Cart for the specified user.
     * @param user the user to whom this cart belongs
     */
    public Cart(User user) {
        this.user = user;
    }

    /**
     * Gets the ID of this cart.
     * @return the cart ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of this cart.
     * @param id the new ID for this cart
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this cart.
     * @return the user of this cart
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this cart.
     * @param user the user to associate with this cart
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the list of items in this cart.
     * @return a list of cart items
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * Sets the list of items in this cart.
     * @param items the new list of items for this cart
     */
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /**
     * Adds an item to the cart. Also sets this cart as the item's associated cart.
     * @param item the item to add to the cart
     */
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    /**
     * Removes an item from the cart. Also disassociates this cart from the item.
     * @param item the item to remove from the cart
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}

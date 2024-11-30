package com.bookstore.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "cart_items")
public class CartItem {

    @Id
    private String id;

    @DBRef
    private Book book;

    private int quantity;

    @DBRef
    private Cart cart;

    /**
     * Default constructor for CartItem
     */
    public CartItem() {
    }

    /**
     * Constructs a CartItem with the specified book, quantity, and cart.
     * @param book - the book associated with this CartItem
     * @param quantity - the quantity of the book
     * @param cart - the cart that this CartItem belongs to
     */
    public CartItem(Book book, int quantity, Cart cart) {
        this.book = book;
        this.quantity = quantity;
        this.cart = cart;
    }

    /**
     * Gets the unique identifier for this CartItem.
     * @return the id of the CartItem
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this CartItem.
     * @param id the id to set for the CartItem
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the book associated with this CartItem.
     * @return the book of the CartItem
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book for this CartItem.
     * @param book the book to associate with this CartItem
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the quantity of the book in the cart.
     * @return the quantity of the book
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the book in the cart.
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the cart that this CartItem belongs to.
     * @return the cart containing this CartItem
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the cart that this CartItem belongs to.
     * @param cart the cart to associate with this CartItem
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }
}

package com.bookstore.dto;

/**
 * Data Transfer Object representing a cart item.
 * Contains the book ID and quantity of the item.
 * Used for adding and removing items from the cart.
 */
public class CartItemDTO {
    private Long bookId;
    private int quantity;

    public CartItemDTO() {}

    /**
     * Constructs a new CartItemDTO with the specified book ID and quantity.
     * @param bookId the ID of the book
     * @param quantity the quantity of the book
     */
    public CartItemDTO(Long bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    /**
     * Gets the ID of the book.
     * @return the book ID
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * Sets the ID of the book.
     * @param bookId the new book ID
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * Gets the quantity of the book.
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the book.
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

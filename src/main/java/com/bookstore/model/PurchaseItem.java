package com.bookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "purchaseItems")
public class PurchaseItem {
    @Id
    private String id;
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private Double purchasePrice;
    private Integer quantity;
    private String purchaseId;

    public PurchaseItem() {}

    /**
     * Represents an individual item in a purchase order, storing book details at time of purchase.
     */
    public PurchaseItem(Book book, Integer quantity, Checkout purchase) {
        if (book != null) {
            this.bookId = book.getId();
            this.isbn = book.getIsbn();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.purchasePrice = book.getPrice();
        }
        this.quantity = quantity;
        if (purchase != null) {
            this.purchaseId = purchase.getId();
        }
    }

    /**
     * Gets the unique identifier for this PurchaseItem.
     * @return the id of the PurchaseItem
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this PurchaseItem.
     * @param id the id to set for the PurchaseItem
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the book associated with this PurchaseItem.
     * @return the book ID
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets the ID of the book associated with this PurchaseItem.
     * @param bookId the book ID to set
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    /**
     * Gets the ISBN of the book associated with this PurchaseItem.
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book associated with this PurchaseItem.
     * @param isbn the ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the title of the book associated with this PurchaseItem.
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book associated with this PurchaseItem.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book associated with this PurchaseItem.
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book associated with this PurchaseItem.
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the purchase price of the book associated with this PurchaseItem.
     * @return the purchase price of the book
     */
    public Double getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the purchase price of the book associated with this PurchaseItem.
     * @param purchasePrice the purchase price to set
     */
    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * Gets the quantity of the book associated with this PurchaseItem.
     * @return the quantity of the book
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the book associated with this PurchaseItem.
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
}
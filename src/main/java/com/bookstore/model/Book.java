package com.bookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class Book {

    @Id
    private String id; // Primary key

    @Indexed(unique = true)
    private String isbn;

    private String title;
    private String description;
    private String author;
    private String publisher;
    private String pictureURL;
    private double price;
    private int inventory; // Available stock

    public Book() {}

    /**
     * Constructs a new Book with the specified details.
     *
     * @param title - the title of the book
     * @param description - a brief description of the book
     * @param author - the author of the book
     * @param publisher - the publisher of the book
     * @param pictureURL - the URL of the book's cover picture
     * @param price - the price of the book
     * @param inventory - the number of copies available in stock
     */
    public Book(String isbn, String title, String description, String author,
                String publisher, String pictureURL, Double price, Integer inventory) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.pictureURL = pictureURL;
        setPrice(price);
        setInventory(inventory);
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     * @param isbn the new ISBN of the book
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the title of the book.
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     * @param title the new title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the book.
     * @return the description of the book
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     * @param description the new description of the book
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the author of the book.
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     * @param author the new author of the book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publisher of the book.
     * @return the publisher of the book
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     * @param publisher the new publisher of the book
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the URL of the book's cover picture.
     * @return the picture URL of the book
     */
    public String getPictureURL() {
        return pictureURL;
    }

    /**
     * Sets the URL of the book's cover picture.
     * @param pictureURL the new picture URL of the book
     */
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    /**
     * Gets the price of the book.
     * @return the price of the book
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price of the book.
     * Throws IllegalArgumentException if price is negative or null.
     * @param price the new price of the book
     */
    public void setPrice(Double price) {
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price cannot be negative or null");
        }
        this.price = price;
    }

    /**
     * Gets the available inventory of the book.
     * @return the inventory of the book
     */
    public Integer getInventory() {
        return inventory;
    }

    /**
     * Sets the available inventory of the book.
     * Throws IllegalArgumentException if inventory is negative or null.
     * @param inventory the new inventory of the book
     */
    public void setInventory(Integer inventory) {
        if (inventory == null || inventory < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative or null");
        }
        this.inventory = inventory;
    }

    /**
     * Gets the ID of this book.
     * @return the ID of this book
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of this book.
     * @param id the new ID for this book
     */
    public void setId(String id) {
        this.id = id;
    }
}

package model;

import org.junit.Before;
import org.junit.Test;
import com.bookstore.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CheckoutTest {

    private Checkout checkout;
    private User user;
    private List<PurchaseItem> items;
    private LocalDateTime purchaseDate;

    @Before
    public void setUp() {
        user = new User("customerUser", Role.CUSTOMER);
        user.setId("user123"); // Set a test ID for the user
        items = new ArrayList<>();

        Book book = new Book(
                "Test ISBN",
                "Test Title",
                "Test Description",
                "Test Author",
                "Test Publisher",
                "http://example.com/test.jpg",
                10.00,
                100
        );
        book.setId("book123"); // Set a test ID for the book

        checkout = new Checkout(user); // Create checkout first
        PurchaseItem item = new PurchaseItem(book, 2, checkout);
        items.add(item);
        checkout.setItems(items);

        purchaseDate = LocalDateTime.now();
    }

    @Test
    public void getId() {
        checkout.setId("1");
        assertEquals("1", checkout.getId());
    }

    @Test
    public void setId() {
        checkout.setId("2");
        assertEquals("2", checkout.getId());
    }

    @Test
    public void getUserId() {
        assertEquals(user.getId(), checkout.getUserId());
    }

    @Test
    public void setUserId() {
        String newUserId = "user456";
        checkout.setUserId(newUserId);
        assertEquals(newUserId, checkout.getUserId());
    }

    @Test
    public void getItems() {
        assertEquals(items, checkout.getItems());
        assertEquals(1, checkout.getItems().size());
        assertEquals(items.get(0), checkout.getItems().get(0));
        assertEquals("Test Title", checkout.getItems().get(0).getTitle());
        assertEquals(Integer.valueOf(2), checkout.getItems().get(0).getQuantity());
    }

    @Test
    public void setItems() {
        List<PurchaseItem> newItems = new ArrayList<>();
        Book newBook = new Book(
                "New ISBN",
                "New Book",
                "Description",
                "Author",
                "Publisher",
                "http://example.com/new.jpg",
                19.99,
                50
        );
        newBook.setId("book456"); // Set a test ID for the new book

        PurchaseItem newItem = new PurchaseItem(newBook, 3, checkout);
        newItems.add(newItem);

        checkout.setItems(newItems);
        assertEquals(newItems, checkout.getItems());
        assertEquals(1, checkout.getItems().size());
        assertEquals(newItem, checkout.getItems().get(0));
        assertEquals("New Book", checkout.getItems().get(0).getTitle());
        assertEquals(Integer.valueOf(3), checkout.getItems().get(0).getQuantity());
    }

    @Test
    public void getPurchaseDate() {
        assertNotNull(checkout.getPurchaseDate());
        assertTrue(purchaseDate.isBefore(checkout.getPurchaseDate()) ||
                purchaseDate.isEqual(checkout.getPurchaseDate()));
    }

    @Test
    public void setPurchaseDate() {
        LocalDateTime newPurchaseDate = LocalDateTime.now().plusDays(1);
        checkout.setPurchaseDate(newPurchaseDate);
        assertEquals(newPurchaseDate, checkout.getPurchaseDate());
    }

    @Test
    public void addItem() {
        Book newBook = new Book(
                "ISBN2",
                "Title2",
                "Desc2",
                "Author2",
                "Publisher2",
                "http://example.com/test2.jpg",
                20.00,
                50
        );
        newBook.setId("book789");

        PurchaseItem newItem = new PurchaseItem(newBook, 1, checkout);
        checkout.addItem(newItem);

        assertEquals(2, checkout.getItems().size());
        assertTrue(checkout.getItems().contains(newItem));
    }

    @Test
    public void removeItem() {
        PurchaseItem itemToRemove = checkout.getItems().get(0);
        checkout.removeItem(itemToRemove);
        assertEquals(0, checkout.getItems().size());
        assertFalse(checkout.getItems().contains(itemToRemove));
    }

    @Test
    public void setItemsWithNull() {
        checkout.setItems(null);
        assertNotNull(checkout.getItems());
        assertTrue(checkout.getItems().isEmpty());
    }
}
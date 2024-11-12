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
        PurchaseItem item = new PurchaseItem(book, 2, checkout);
        items.add(item);

        purchaseDate = LocalDateTime.now();
        checkout = new Checkout(user, items);
    }

    @Test
    public void getId() {
        checkout.setId(1L);
        assertEquals(Long.valueOf(1L), checkout.getId());
    }

    @Test
    public void setId() {
        checkout.setId(2L);
        assertEquals(Long.valueOf(2L), checkout.getId());
    }

    @Test
    public void getUser() {
        assertEquals(user, checkout.getUser());
        assertEquals("customerUser", checkout.getUser().getUsername());
        assertEquals(Role.CUSTOMER, checkout.getUser().getRole());
    }

    @Test
    public void setUser() {
        User newCustomerUser = new User("newCustomer", Role.CUSTOMER);
        checkout.setUser(newCustomerUser);
        assertEquals(newCustomerUser, checkout.getUser());
        assertEquals("newCustomer", checkout.getUser().getUsername());
        assertEquals(Role.CUSTOMER, checkout.getUser().getRole());
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
        Book newBook = new Book("New ISBN", "New Book", "Description", "Author", "Publisher",
                "http://example.com/new.jpg", 19.99, 50);
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
        assertTrue(purchaseDate.isBefore(checkout.getPurchaseDate()) || purchaseDate.isEqual(checkout.getPurchaseDate()));
    }

    @Test
    public void setPurchaseDate() {
        LocalDateTime newPurchaseDate = LocalDateTime.now().plusDays(1);
        checkout.setPurchaseDate(newPurchaseDate);
        assertEquals(newPurchaseDate, checkout.getPurchaseDate());
    }
}

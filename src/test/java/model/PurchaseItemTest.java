package model;

import org.junit.Before;
import org.junit.Test;

import com.bookstore.model.*;
import static org.junit.Assert.*;

public class PurchaseItemTest {

    private PurchaseItem purchaseItem;
    private Book book;
    private Checkout checkout;

    @Before
    public void setUp() {
        book = new Book(
                "1",
                "Test ISBN",
                "Test Title",
                "Test Description",
                "Test Author",
                "Test Publisher",
                10.00,
                10
        );
        checkout = new Checkout();
        checkout.setId("purchase123");
        purchaseItem = new PurchaseItem(book, 2, checkout);
    }

    @Test
    public void testGetId() {
        purchaseItem.setId("1");
        assertEquals("1", purchaseItem.getId());
    }

    @Test
    public void testSetId() {
        purchaseItem.setId("5");
        assertEquals("5", purchaseItem.getId());
    }

    @Test
    public void testGetBookId() {
        assertEquals(book.getId(), purchaseItem.getBookId());
    }

    @Test
    public void testSetBookId() {
        String newBookId = "2";
        purchaseItem.setBookId(newBookId);
        assertEquals(newBookId, purchaseItem.getBookId());
    }

    @Test
    public void testGetIsbn() {
        assertEquals(book.getIsbn(), purchaseItem.getIsbn());
    }

    @Test
    public void testSetIsbn() {
        String newIsbn = "New ISBN";
        purchaseItem.setIsbn(newIsbn);
        assertEquals(newIsbn, purchaseItem.getIsbn());
    }

    @Test
    public void testGetTitle() {
        assertEquals(book.getTitle(), purchaseItem.getTitle());
    }

    @Test
    public void testSetTitle() {
        String newTitle = "New Title";
        purchaseItem.setTitle(newTitle);
        assertEquals(newTitle, purchaseItem.getTitle());
    }

    @Test
    public void testGetAuthor() {
        assertEquals(book.getAuthor(), purchaseItem.getAuthor());
    }

    @Test
    public void testSetAuthor() {
        String newAuthor = "New Author";
        purchaseItem.setAuthor(newAuthor);
        assertEquals(newAuthor, purchaseItem.getAuthor());
    }

    @Test
    public void testGetPurchasePrice() {
        assertEquals(book.getPrice(), purchaseItem.getPurchasePrice(), 0.01);
    }

    @Test
    public void testSetPurchasePrice() {
        Double newPurchasePrice = 17.99;
        purchaseItem.setPurchasePrice(newPurchasePrice);
        assertEquals(newPurchasePrice, purchaseItem.getPurchasePrice(), 0.01);
    }

    @Test
    public void testGetQuantity() {
        assertEquals((Integer) 2, purchaseItem.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        Integer newQuantity = 5;
        purchaseItem.setQuantity(newQuantity);
        assertEquals(newQuantity, purchaseItem.getQuantity());
    }

    @Test
    public void testGetPurchaseId() {
        assertEquals(checkout.getId(), purchaseItem.getPurchaseId());
    }

    @Test
    public void testSetPurchaseId() {
        String newPurchaseId = "newPurchaseId";
        purchaseItem.setPurchaseId(newPurchaseId);
        assertEquals(newPurchaseId, purchaseItem.getPurchaseId());
    }
}

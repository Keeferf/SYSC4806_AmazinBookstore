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
                "Test ISBN",
            "Test Title",
            "Test Description",
            "Test Author",
            "Test Publisher",
            "http://example.com/test.jpg",
            10.00,
            10
        );
        checkout = new Checkout();
        purchaseItem = new PurchaseItem(book, 2, checkout);
    }

    @Test
    public void getId() {
        Long id = 1L;
        purchaseItem.setId(id);
        assertEquals(id, purchaseItem.getId());
    }

    @Test
    public void setId() {
        Long id = 5L;
        purchaseItem.setId(id);
        assertEquals(id, purchaseItem.getId());
    }

    @Test
    public void getBookId() {
        assertEquals(book.getId(), purchaseItem.getBookId().longValue());
    }

    @Test
    public void setBook(){
        Long newBookId = 2L;
        purchaseItem.setBookId(newBookId);
        assertEquals(newBookId, purchaseItem.getBookId());
    }

    @Test
    public void getIsbn(){
        assertEquals(book.getIsbn(), purchaseItem.getIsbn());
    }

    @Test
    public void setIsbn(){
        String newIsbn = "New ISBN";
        purchaseItem.setIsbn(newIsbn);
        assertEquals(newIsbn, purchaseItem.getIsbn());
    }

    @Test
    public void getTitle(){
        assertEquals(book.getTitle(), purchaseItem.getTitle());
    }

    @Test
    public void setTitle(){
        String newTitle = "New Title";
        purchaseItem.setTitle(newTitle);
        assertEquals(newTitle, purchaseItem.getTitle());
    }

    @Test
    public void getAuthor(){
        assertEquals(book.getAuthor(), purchaseItem.getAuthor());
    }

    @Test
    public void setAuthor(){
        String newAuthor = "New Author";
        purchaseItem.setAuthor(newAuthor);
        assertEquals(newAuthor, purchaseItem.getAuthor());
    }

    @Test
    public void getPurchasePrice(){
        assertEquals(book.getPrice(), purchaseItem.getPurchasePrice());
    }

    @Test
    public void setPurchasePrice(){
        Double newPurchasePrice = 17.99;
        purchaseItem.setPurchasePrice(newPurchasePrice);
        assertEquals(newPurchasePrice, purchaseItem.getPurchasePrice());
    }

    @Test
    public void getQuantity() {
        assertEquals((Integer) 2, purchaseItem.getQuantity());
    }

    @Test
    public void setQuantity() {
        Integer newQuantity = 5;
        purchaseItem.setQuantity(newQuantity);
        assertEquals(newQuantity, purchaseItem.getQuantity());
    }

    @Test
    public void getPurchase() {
        assertEquals(checkout, purchaseItem.getPurchase());
    }

    @Test
    public void setPurchase() {
        Checkout newCheckout = new Checkout();
        purchaseItem.setPurchase(newCheckout);
        assertEquals(newCheckout, purchaseItem.getPurchase());
    }
}

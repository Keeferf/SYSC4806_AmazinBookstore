package model;

import com.bookstore.model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CartItemTest {

    private CartItem cartItem;
    private Book book;
    private Cart cart;

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
                100
        );

        cart = new Cart();
        cartItem = new CartItem(book, 5, cart);
    }

    @Test
    public void getId() {
        cartItem.setId(1L);
        assertEquals(Long.valueOf(1L), cartItem.getId());
    }

    @Test
    public void setId() {
        cartItem.setId(2L);
        assertEquals(Long.valueOf(2L), cartItem.getId());
    }

    @Test
    public void getBook() {
        assertEquals(book, cartItem.getBook());
    }

    @Test
    public void setBook() {
        Book newBook = new Book(
                "New ISBN",
                "New Title",
                "New Description",
                "New Author",
                "New Publisher",
                "http://example.com/new.jpg",
                39.99,
                50
        );
        cartItem.setBook(newBook);
        assertEquals(newBook, cartItem.getBook());
    }

    @Test
    public void getQuantity() {
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    public void setQuantity() {
        cartItem.setQuantity(10);
        assertEquals(10, cartItem.getQuantity());
    }

    @Test
    public void getCart() {
        assertEquals(cart, cartItem.getCart());
    }

    @Test
    public void setCart() {
        Cart newCart = new Cart();
        cartItem.setCart(newCart);
        assertEquals(newCart, cartItem.getCart());
    }

    /**
     * Use the addItem method from Cart to add the item and test the association
     */
    @Test
    public void addItemToCart() {
        cart.addItem(cartItem);
        assertTrue(cart.getItems().contains(cartItem));
        assertEquals(cart, cartItem.getCart());
    }

    /**
     * Add and then remove the item, checking disassociation
     */
    @Test
    public void removeItemFromCart() {
        cart.addItem(cartItem);
        cart.removeItem(cartItem);
        assertFalse(cart.getItems().contains(cartItem));
        assertNull(cartItem.getCart());
    }
}

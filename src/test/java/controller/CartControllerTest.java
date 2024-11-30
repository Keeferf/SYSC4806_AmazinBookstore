package controller;

import com.bookstore.controller.CartController;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.model.User;
import com.bookstore.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartController cartController;

    private Cart cart;
    private CartItem cartItem;
    private Book book;
    private User user;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("1L");
        user.setUsername("testUser");

        book = new Book();
        book.setId("1");
        book.setTitle("Test Book");
        book.setPrice(29.99);

        cartItem = new CartItem();
        cartItem.setId("1L");
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId("1L");
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
    }

    @Test
    void tearDown() throws Exception {
        if (autoCloseable != null) {
            autoCloseable.close();
        }
    }

    /**
     * Tests adding an item to an existing cart successfully
     */
    @Test
    void testAddToExistingCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<?> response = cartController.addToCart(cartItem, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item successfully added to cart", response.getBody());
    }

    /**
     * Tests adding an item when the cart doesn't exist yet
     */
    @Test
    void testAddToNewCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<?> response = cartController.addToCart(cartItem, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Tests error handling when trying to add a null item to cart
     */
    @Test
    void testAddNullItem() {
        ResponseEntity<?> response = cartController.addToCart(null, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cart item and user ID cannot be null", response.getBody());
    }

    /**
     * Tests error handling when database save operation fails
     */
    @Test
    void testAddWithDatabaseError() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = cartController.addToCart(cartItem, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    /**
     * Tests removing an item from cart successfully
     */
    @Test
    void testRemoveFromCart() {
        cart.getItems().add(cartItem);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<?> response = cartController.removeFromCart(cartItem, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Item successfully removed from cart", response.getBody());
    }

    /**
     * Tests removing an item that doesn't exist in the cart
     */
    @Test
    void testRemoveNonexistentItem() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        ResponseEntity<?> response = cartController.removeFromCart(cartItem, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests error handling when trying to remove from cart with null user ID
     */
    @Test
    void testRemoveWithNullUserId() {
        ResponseEntity<?> response = cartController.removeFromCart(cartItem, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cart item and user ID cannot be null", response.getBody());
    }

    /**
     * Tests clearing all items from cart successfully
     */
    @Test
    void testClearCart() {
        cart.getItems().add(cartItem);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<?> response = cartController.clearCart(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(cart.getItems().isEmpty());
    }

    /**
     * Tests clearing a cart that doesn't exist yet
     */
    @Test
    void testClearNonexistentCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());

        ResponseEntity<?> response = cartController.clearCart(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Tests error handling when clearing cart fails due to database error
     */
    @Test
    void testClearWithDatabaseError() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = cartController.clearCart(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((String)response.getBody()).contains("Failed to clear cart"));
    }
}
package controller;

import com.bookstore.controller.PurchaseController;
import com.bookstore.dto.CartItemDTO;
import com.bookstore.dto.PurchaseDTO;
import com.bookstore.model.*;
import com.bookstore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PurchaseControllerTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private PurchaseController purchaseController;

    private User customer;
    private Book book;
    private CartItemDTO cartItem;
    private Checkout checkout;
    private PurchaseItem purchaseItem;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        // Create book with proper constructor
        book = new Book("1234567890", "Test Book", "Test Description",
                "Test Author", "Test Publisher", "test-url", 29.99, 10);
        book.setId("1"); // Set ID separately

        // Create customer
        customer = new User("customer", Role.CUSTOMER);
        customer.setId("1");

        // Create cart item
        cartItem = new CartItemDTO("1", 2, "Test Book", "Test Author", "1234567890", 29.99);

        // Create checkout
        checkout = new Checkout(customer);
        checkout.setId("1");
        checkout.setPurchaseDate(LocalDateTime.now());

        // Create purchase item
        purchaseItem = new PurchaseItem(book, 2, checkout);

        // Add item to checkout
        checkout.addItem(purchaseItem);

        when(principal.getName()).thenReturn("customer");
    }

    @Test
    void tearDown() throws Exception {
        if (autoCloseable != null) {
            autoCloseable.close();
        }
    }

    @Test
    void testSuccessfulCheckout() {
        List<CartItemDTO> cartItems = Collections.singletonList(cartItem);
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customer));
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        when(checkoutRepository.save(any(Checkout.class))).thenReturn(checkout);

        ResponseEntity<String> response = purchaseController.checkout(cartItems, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Checkout successful", response.getBody());
        verify(bookRepository).save(book);
        assertEquals(8, book.getInventory()); // Verify inventory was reduced
    }

    @Test
    void testCheckoutInsufficientInventory() {
        cartItem = new CartItemDTO("1", 15, "Test Book", "Test Author", "1234567890", 29.99);
        List<CartItemDTO> cartItems = Collections.singletonList(cartItem);
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customer));
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));

        ResponseEntity<String> response = purchaseController.checkout(cartItems, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Not enough inventory for book"));
    }

    @Test
    void testCheckoutNonexistentBook() {
        List<CartItemDTO> cartItems = Collections.singletonList(cartItem);
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customer));
        when(bookRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = purchaseController.checkout(cartItems, principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Book not found"));
    }

    @Test
    void testCheckoutInvalidUser() {
        List<CartItemDTO> cartItems = Collections.singletonList(cartItem);
        when(userRepository.findByUsername("customer")).thenReturn(Optional.empty());

        ResponseEntity<String> response = purchaseController.checkout(cartItems, principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("User not found"));
    }

    @Test
    void testViewPurchaseHistory() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customer));
        when(checkoutRepository.findByUserId(customer.getId())).thenReturn(Collections.singletonList(checkout));

        ResponseEntity<List<PurchaseDTO>> response = purchaseController.getPurchaseHistory(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        PurchaseDTO purchase = response.getBody().get(0);
        assertEquals(checkout.getId(), purchase.getId());
        assertEquals(checkout.getPurchaseDate(), purchase.getPurchaseDate());
        assertEquals(1, purchase.getItems().size());

        CartItemDTO item = purchase.getItems().get(0);
        assertEquals(book.getId(), item.getBookId());
        assertEquals(2, item.getQuantity());
        assertEquals(book.getTitle(), item.getTitle());
    }

    @Test
    void testViewEmptyPurchaseHistory() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customer));
        when(checkoutRepository.findByUserId(customer.getId())).thenReturn(new ArrayList<>());

        ResponseEntity<List<PurchaseDTO>> response = purchaseController.getPurchaseHistory(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testViewPurchaseHistoryInvalidUser() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.empty());

        ResponseEntity<List<PurchaseDTO>> response = purchaseController.getPurchaseHistory(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
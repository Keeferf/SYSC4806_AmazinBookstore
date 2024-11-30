package controller;

import com.bookstore.controller.BookController;
import com.bookstore.model.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CheckoutRepository;
import com.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private BookController bookController;

    private Book book1;
    private Book book2;
    private User admin;
    private User customer;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        // Changed constructor calls to match Book.java's constructor
        book1 = new Book("1234567890", "Test Book 1", "Description 1",
                "Author 1", "Publisher 1", "url1", 29.99, 10);
        book1.setId("1");

        book2 = new Book("0987654321", "Test Book 2", "Description 2",
                "Author 2", "Publisher 2", "url2", 19.99, 5);
        book2.setId("2");

        admin = new User("admin", Role.ADMIN);
        admin.setId("1");
        customer = new User("customer", Role.CUSTOMER);
        customer.setId("2");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Setup principal mock
        when(principal.getName()).thenReturn(customer.getUsername());
    }

    @Test
    void tearDown() throws Exception {
        if (autoCloseable != null) {
            autoCloseable.close();
        }
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookController.getAllBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Book 1", result.get(0).getTitle());
        assertEquals("Test Book 2", result.get(1).getTitle());
    }

    @Test
    void testGetBookByIdWhenExists() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(book1));

        ResponseEntity<Book> response = bookController.getBookById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book1.getTitle(), response.getBody().getTitle());
    }

    @Test
    void testGetBookByIdWhenNotFound() {
        when(bookRepository.findById("3")).thenReturn(Optional.empty());

        ResponseEntity<Book> response = bookController.getBookById("3");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSearchBooksByTitleWithMatches() {
        when(bookRepository.findByTitleContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = bookController.searchBooks("Test");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(book -> book.getTitle().equals("Test Book 1")));
    }

    @Test
    void testSearchBooksByTitleNoMatches() {
        when(bookRepository.findByTitleContainingIgnoreCase("Nonexistent"))
                .thenReturn(new ArrayList<>());

        List<Book> result = bookController.searchBooks("Nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchBooksByIsbn() {
        when(bookRepository.findByIsbnContainingIgnoreCase("123"))
                .thenReturn(Collections.singletonList(book1));

        ResponseEntity<List<Book>> response = bookController.searchBooksByIsbn("123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("1234567890", response.getBody().get(0).getIsbn());
    }

    @Test
    void testSearchBooksByAuthor() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Author 1"))
                .thenReturn(Collections.singletonList(book1));

        List<Book> result = bookController.searchBooksByAuthor("Author 1");

        assertEquals(1, result.size());
        assertEquals("Author 1", result.get(0).getAuthor());
    }

    @Test
    void testUploadBookAsAdmin() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        ResponseEntity<?> response = bookController.uploadBook(book1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUploadBookWithDuplicateIsbn() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.save(any(Book.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate ISBN"));

        ResponseEntity<?> response = bookController.uploadBook(book1);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateBookWhenExists() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.findById("1")).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        Book updatedBook = new Book("1234567890", "Updated Title", null,
                null, null, null, 0.0, 0);
        updatedBook.setId("1");

        ResponseEntity<?> response = bookController.editBook("1", updatedBook);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateBookWhenNotFound() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.findById("3")).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookController.editBook("3", book1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteBookWhenExists() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.findById("1")).thenReturn(Optional.of(book1));

        ResponseEntity<?> response = bookController.deleteBook("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRepository).delete(book1);
    }

    @Test
    void testDeleteBookWhenNotFound() {
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(bookRepository.findById("3")).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookController.deleteBook("3");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetRecommendationsWithSimilarUsers() {
        // Create test books
        Book book3 = new Book("3333333333", "Test Book 3", "Description 3",
                "Author 3", "Publisher 3", "url3", 39.99, 15);
        book3.setId("3");

        // Create purchase histories
        PurchaseItem item1 = new PurchaseItem(book1, 1, null);
        PurchaseItem item2 = new PurchaseItem(book2, 1, null);
        PurchaseItem item3 = new PurchaseItem(book3, 1, null);

        // Setup checkouts
        Checkout customerCheckout = new Checkout(customer);
        customerCheckout.addItem(item1);
        customerCheckout.addItem(item2);

        User similarUser = new User("similar", Role.CUSTOMER);
        similarUser.setId("3");
        Checkout similarUserCheckout = new Checkout(similarUser);
        similarUserCheckout.addItem(item2);
        similarUserCheckout.addItem(item3);

        // Mock repository calls
        when(userRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));
        when(checkoutRepository.findByUserId(customer.getId())).thenReturn(Collections.singletonList(customerCheckout));
        when(userRepository.findAll()).thenReturn(Arrays.asList(customer, similarUser));
        when(checkoutRepository.findByUserId(similarUser.getId())).thenReturn(Collections.singletonList(similarUserCheckout));
        when(bookRepository.findAllById(any())).thenReturn(Collections.singletonList(book3));

        ResponseEntity<List<Book>> response = bookController.getRecommendedBooks(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(book3.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetRecommendationsWithNoPurchaseHistory() {
        when(userRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));
        when(checkoutRepository.findByUserId(customer.getId())).thenReturn(new ArrayList<>());

        ResponseEntity<List<Book>> response = bookController.getRecommendedBooks(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testGetRecommendationsWithNoSimilarUsers() {
        // Setup checkout with only one book
        PurchaseItem item1 = new PurchaseItem(book1, 1, null);
        Checkout checkout = new Checkout(customer);
        checkout.addItem(item1);

        when(userRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));
        when(checkoutRepository.findByUserId(customer.getId())).thenReturn(Collections.singletonList(checkout));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(customer));

        ResponseEntity<List<Book>> response = bookController.getRecommendedBooks(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testGetRecommendationsWithInvalidUser() {
        when(principal.getName()).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                bookController.getRecommendedBooks(principal));
    }
}
package controller;

import com.bookstore.BookStoreApplication;
import com.bookstore.model.*;
import com.bookstore.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BookStoreApplication.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private User adminUser;
    private User customerUser;
    private Book book;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);

        // Execute database cleanup in a transaction
        transactionTemplate.execute(status -> {
            entityManager.createNativeQuery("DELETE FROM cart_item").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM purchase_item").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM checkout").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM cart").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM book").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM users").executeUpdate();

            // Create test users
            adminUser = new User("adminUser", Role.ADMIN);
            customerUser = new User("customerUser", Role.CUSTOMER);
            userRepository.save(adminUser);
            userRepository.save(customerUser);

            // Create test book
            book = new Book(
                    "Test ISBN",
                    "Test Title",
                    "Test Description",
                    "Test Author",
                    "Test Publisher",
                    "http://example.com/test.jpg",
                    10.00,
                    10);
            bookRepository.save(book);

            return null;
        });
    }

    // Test methods remain the same
    @Test
    public void getAllBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));
    }

    @Test
    public void getBookById_CaseFound() throws Exception {
        mockMvc.perform(get("/api/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    public void getBookById_CaseNotFound() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchBooks() throws Exception {
        mockMvc.perform(get("/api/books/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));
    }

    @Test
    public void searchBooksByAuthor() throws Exception {
        mockMvc.perform(get("/api/books/search/author")
                        .param("author", "Test Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value(book.getAuthor()));
    }

    @Test
    public void searchBooksByPublisher() throws Exception {
        mockMvc.perform(get("/api/books/search/publisher")
                        .param("publisher", "Test Publisher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publisher").value(book.getPublisher()));
    }

    @Test
    public void filterBooksByPrice() throws Exception {
        mockMvc.perform(get("/api/books/filter/price")
                        .param("minPrice", "5.0")
                        .param("maxPrice", "15.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(book.getPrice()));
    }

    @Test
    public void filterBooksByInventory() throws Exception {
        mockMvc.perform(get("/api/books/filter/inventory")
                        .param("minInventory", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inventory").value(book.getInventory()));
    }

    @Test
    public void uploadBook_AdminAccess() throws Exception {
        String newBookJson = "{\"isbn\":\"123456789\", \"title\":\"New Book\", \"price\":15.99}";

        mockMvc.perform(post("/api/books")
                        .param("userId", adminUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    public void uploadBook_CustomerAccessDenied() throws Exception {
        String newBookJson = "{\"isbn\":\"123456789\", \"title\":\"New Book\", \"price\":15.99}";

        mockMvc.perform(post("/api/books")
                        .param("userId", customerUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied."));
    }

    @Test
    public void editBook_AdminAccess() throws Exception {
        String updatedBookJson = "{\"isbn\":\"987654321\", \"title\":\"Updated Book\", \"price\":25.99}";

        mockMvc.perform(put("/api/books/" + book.getId())
                        .param("userId", adminUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    public void editBook_CustomerAccessDenied() throws Exception {
        String updatedBookJson = "{\"isbn\":\"987654321\", \"title\":\"Updated Book\", \"price\":25.99}";

        mockMvc.perform(put("/api/books/" + book.getId())
                        .param("userId", customerUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied."));
    }

    @Test
    public void deleteBook_AdminAccess() throws Exception {
        mockMvc.perform(delete("/api/books/" + book.getId())
                        .param("userId", adminUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully."));
    }

    @Test
    public void deleteBook_CustomerAccessDenied() throws Exception {
        mockMvc.perform(delete("/api/books/" + book.getId())
                        .param("userId", customerUser.getId().toString()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied."));
    }
}
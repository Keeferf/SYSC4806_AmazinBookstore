package controller;

import com.bookstore.BookStoreApplication;
import com.bookstore.model.Book;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CheckoutRepository;
import com.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = BookStoreApplication.class)
@AutoConfigureMockMvc
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    private User customerUser;
    private Book book;

    @BeforeEach
    public void setUp() {

        checkoutRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save sample user and book
        customerUser = new User("customerUser", Role.CUSTOMER);
        userRepository.save(customerUser);

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
        bookRepository.save(book);
    }

    @Test
    public void successfulCheckout() throws Exception {
        mockMvc.perform(post("/api/purchase/checkout")
                        .param("userId", customerUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"bookId\":" + book.getId() + ",\"quantity\":2}]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Checkout successful."));

        Book updatedBook = bookRepository.findById((long)book.getId()).orElseThrow();
        assert updatedBook.getInventory() == 8 : "Inventory not updated correctly after checkout";
    }

    @Test
    public void bookNotFound() throws Exception {
        Long id = 999L;

        mockMvc.perform(post("/api/purchase/checkout")
                        .param("userId", customerUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"bookId\":" + id + ",\"quantity\":1}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Book with ID " + id + " not found."));
    }

    @Test
    public void emptyInventory() throws Exception {
        book.setInventory(0);
        bookRepository.save(book);

        mockMvc.perform(post("/api/purchase/checkout")
                        .param("userId", customerUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"bookId\":" + book.getId() + ",\"quantity\":1}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough inventory for book: " + book.getTitle()));
    }
}
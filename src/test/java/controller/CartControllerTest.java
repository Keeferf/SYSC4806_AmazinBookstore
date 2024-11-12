package controller;

import com.bookstore.BookStoreApplication;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
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
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    private Long userId;
    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        cartRepository.deleteAll();

        Book book = new Book(
                "Test ISBN",
                "Test Title",
                "Test Description",
                "Test Author",
                "Test Publisher",
                "http://example.com/test.jpg",
                10.00,
                5
        );
        book = bookRepository.save(book);

        Cart cart = new Cart();
        cartRepository.save(cart);
        userId = cart.getId();

        cartItem = new CartItem(book, 1, cart);
    }

    @Test
    public void addToCart() throws Exception {
        String cartItemJson = "{ \"book\": { \"id\": " + cartItem.getBook().getId() + " }, \"quantity\": 1 }";

        mockMvc.perform(post("/api/cart/add")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartItemJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Item successfully added to cart"));
    }
}
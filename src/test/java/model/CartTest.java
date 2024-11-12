package model;

import com.bookstore.model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class CartTest {

    private Cart adminCart;
    private Cart customerCart;
    private User adminUser;
    private User customerUser;
    private CartItem cartItem;

    @Before
    public void setUp() {
        adminUser = new User("adminUser", Role.ADMIN);
        adminCart = new Cart(adminUser);

        customerUser = new User("customerUser", Role.CUSTOMER);
        customerCart = new Cart(customerUser);

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
        cartItem = new CartItem(book, 2, adminCart);
    }

    @Test
    public void getId() {
        adminCart.setId(1L);
        assertEquals(Long.valueOf(1L), adminCart.getId());
    }

    @Test
    public void setId() {
        adminCart.setId(2L);
        assertEquals(Long.valueOf(2L), adminCart.getId());
    }

    @Test
    public void getUser() {
        assertEquals(adminUser, adminCart.getUser());
        assertEquals("adminUser", adminCart.getUser().getUsername());
        assertEquals(Role.ADMIN, adminCart.getUser().getRole());

        assertEquals(customerUser, customerCart.getUser());
        assertEquals("customerUser", customerCart.getUser().getUsername());
        assertEquals(Role.CUSTOMER, customerCart.getUser().getRole());
    }

    @Test
    public void setUser() {
        User newAdminUser = new User("newAdmin", Role.ADMIN);
        adminCart.setUser(newAdminUser);
        assertEquals(newAdminUser, adminCart.getUser());
        assertEquals("newAdmin", adminCart.getUser().getUsername());
        assertEquals(Role.ADMIN, adminCart.getUser().getRole());

        User newCustomerUser = new User("newCustomer", Role.CUSTOMER);
        customerCart.setUser(newCustomerUser);
        assertEquals(newCustomerUser, customerCart.getUser());
        assertEquals("newCustomer", customerCart.getUser().getUsername());
        assertEquals(Role.CUSTOMER, customerCart.getUser().getRole());
    }

    @Test
    public void getItems() {
        adminCart.addItem(cartItem);
        List<CartItem> items = adminCart.getItems();
        assertEquals(1, items.size());
        assertTrue(items.contains(cartItem));
    }

    @Test
    public void setItems() {
        List<CartItem> newItems = new ArrayList<>();
        newItems.add(cartItem);

        adminCart.setItems(newItems);
        assertEquals(newItems, adminCart.getItems());
        assertEquals(adminCart, cartItem.getCart());
    }

    @Test
    public void addItem() {
        adminCart.addItem(cartItem);
        assertTrue(adminCart.getItems().contains(cartItem));
        assertEquals(adminCart, cartItem.getCart());
    }

    @Test
    public void removeItem() {
        adminCart.addItem(cartItem);
        adminCart.removeItem(cartItem);
        assertFalse(adminCart.getItems().contains(cartItem));
        assertNull(cartItem.getCart()); 
    }
}

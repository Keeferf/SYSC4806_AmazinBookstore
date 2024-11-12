package model;

import com.bookstore.model.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest {

    private User customerUser;
    private User adminUser;
    private List<Checkout> purchases;

    @Before
    public void setUp() {
        purchases = new ArrayList<>();

        // Initialize customer user
        customerUser = new User("customerUser", Role.CUSTOMER);
        customerUser.setPurchases(purchases);

        // Initialize admin user
        adminUser = new User("adminUser", Role.ADMIN);

    }

    @Test
    public void getId() {
        Long id = 1L;
        customerUser.setId(id);
        assertEquals(id, customerUser.getId());

    }

    @Test
    public void setId() {
        Long id = 2L;
        customerUser.setId(id);
        assertEquals(id, customerUser.getId());
    }

    @Test
    public void getUsername() {
        assertEquals("customerUser", customerUser.getUsername());
        assertEquals("adminUser", adminUser.getUsername());
    }

    @Test
    public void setUsername() {
        customerUser.setUsername("customerUser");
        assertEquals("customerUser", customerUser.getUsername());

        adminUser.setUsername("newAdminUser");
        assertEquals("newAdminUser", adminUser.getUsername());
    }

    @Test
    public void getRole() {
        assertEquals(Role.CUSTOMER, customerUser.getRole());
        assertEquals(Role.ADMIN, adminUser.getRole());
    }

    @Test
    public void setRole() {
        customerUser.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, customerUser.getRole());

        adminUser.setRole(Role.CUSTOMER);
        assertEquals(Role.CUSTOMER, adminUser.getRole());
    }

    @Test
    public void getPurchases() {
        assertEquals(purchases, customerUser.getPurchases());
        assertTrue(customerUser.getPurchases().isEmpty());
    }

    @Test
    public void setPurchases() {
        List<Checkout> newPurchases = new ArrayList<>();
        Checkout checkout = new Checkout();
        newPurchases.add(checkout);
        customerUser.setPurchases(newPurchases);

        assertEquals(newPurchases, customerUser.getPurchases());
        assertEquals(1, customerUser.getPurchases().size());
        assertEquals(checkout, customerUser.getPurchases().get(0));
    }
}
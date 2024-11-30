package controller;

import com.bookstore.controller.UserController;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(Role.CUSTOMER);

        user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRole(Role.ADMIN);
    }

    @Test
    void tearDown() throws Exception {
        if (autoCloseable != null) {
            autoCloseable.close();
        }
    }

    /**
     * Tests retrieving all users
     */
    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    /**
     * Tests creating a new user successfully
     */
    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userController.createUser(user1);

        assertNotNull(result);
        assertEquals(user1.getUsername(), result.getUsername());
        verify(userRepository).save(user1);
    }

    /**
     * Tests retrieving an existing user by ID
     */
    @Test
    void testGetExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user1.getUsername(), response.getBody().getUsername());
    }

    /**
     * Tests retrieving a non-existent user by ID
     */
    @Test
    void testGetNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests updating an existing user successfully
     */
    @Test
    void testUpdateExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User updatedUser = new User();
        updatedUser.setUsername("updatedUsername");
        updatedUser.setRole(Role.ADMIN);

        ResponseEntity<?> response = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User resultUser = (User) response.getBody();
        assertEquals("updatedUsername", resultUser.getUsername());
        assertEquals(Role.ADMIN, resultUser.getRole());
    }

    /**
     * Tests updating a non-existent user
     */
    @Test
    void testUpdateNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUser(999L, user1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests deleting an existing user successfully
     */
    @Test
    void testDeleteExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).delete(user1);
    }

    /**
     * Tests deleting a non-existent user
     */
    @Test
    void testDeleteNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.deleteUser(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Tests creating user with null values
     */
    @Test
    void testCreateNullUser() {
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User result = userController.createUser(new User());

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests updating user with null values
     */
    @Test
    void testUpdateUserWithNullValues() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User emptyUser = new User();
        ResponseEntity<?> response = userController.updateUser(1L, emptyUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests creating user with database error
     */
    @Test
    void testCreateUserDatabaseError() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userController.createUser(user1));
    }
}
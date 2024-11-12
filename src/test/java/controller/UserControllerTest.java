package controller;

import com.bookstore.BookStoreApplication;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BookStoreApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User customerUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        customerUser = new User("customerUser", Role.CUSTOMER);
        userRepository.save(customerUser);
    }

    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(customerUser.getUsername()));
    }

    @Test
    public void createUser() throws Exception {
        String newUser = "{\"username\":\"newUser\", \"role\":\"admin\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    public void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + customerUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(customerUser.getUsername()));
    }

    @Test
    public void getUserById_isNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser() throws Exception {
        String newUser = "{\"username\":\"updatedUser\", \"role\":\"admin\"}";

        mockMvc.perform(put("/api/users/" + customerUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    public void updateUser_isNotFound() throws Exception {
        String newUser = "{\"username\":\"nonExistentUser\", \"role\":\"customer\"}";

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + customerUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + customerUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_isNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
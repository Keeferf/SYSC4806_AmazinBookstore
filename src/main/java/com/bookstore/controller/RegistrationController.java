package com.bookstore.controller;

import com.bookstore.exception.UserExistsException;
import com.bookstore.model.LoginBody;
import com.bookstore.model.RegistrationBody;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    @Autowired
    private UserService userService;


    /**
     * Constructs a new RegistrationController with the specified UserService.
     * @param userService the user service to be used by this controller
     */
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user with the provided registration details.
     * @param registrationBody the registration details including username, password, email, first name, and last name
     * @return a ResponseEntity with status 200 (OK) if registration is successful,
     *         or status 409 (Conflict) if a user with the provided username or email already exists
     */
    @PostMapping("/register")
    public ResponseEntity<String> RegisterUser(@Valid @RequestBody RegistrationBody registrationBody) {
        userService.register(registrationBody);
        return ResponseEntity.ok().body("{\"message\": \"Registration successful\"}");
    }
    /**
     * Authenticates a user and returns a JWT if the login is successful.
     * @param loginBody the login request containing the user's credentials
     * @return a ResponseEntity containing the JWT if authentication is successful,
     *         or a ResponseEntity with status UNAUTHORIZED if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity LoginUser(@Valid @RequestBody LoginBody loginBody) {
        String token = userService.verify(loginBody);
        if(token != null) {
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Authorization failed\"}");
    }

    /**
     * Retrieves the profile information of the currently logged-in user.
     * @param user the currently authenticated user
     * @return the profile information of the logged-in user
     */
    @GetMapping("/me")
    public User getLoggedInUserProfile(@AuthenticationPrincipal User user) {
        return user;
    }
}

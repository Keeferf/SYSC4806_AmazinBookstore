package com.bookstore.service;

import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.RegistrationDTO;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    /**
     * Registers a new user in the system.
     * Creates a new user entity with the provided registration details,
     * encrypts the password, and saves it to the database.
     *
     * @param body the registration information containing username, password, and user details
     * @param role the role to be assigned to the new user
     */
    public void register(RegistrationDTO body, Role role) {
        User user = new User();
        user.setUsername(body.getUsername());
        user.setRole(role);
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setPassword(encoder.encode(body.getPassword()));
        repo.save(user);
    }

    /**
     * Verifies user credentials and generates a JWT token upon successful authentication.
     *
     * @param user the login credentials containing username and password
     * @return JWT token string if authentication is successful, "fail" otherwise
     */
    public String verify(LoginDTO user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            return "fail";
        }
    }
}

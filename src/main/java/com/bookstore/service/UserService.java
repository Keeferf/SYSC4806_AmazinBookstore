package com.bookstore.service;

import com.bookstore.model.LoginBody;
import com.bookstore.model.RegistrationBody;
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

    public void register(RegistrationBody body) {
        User user = new User();
        user.setUsername(body.getUsername());
        user.setRole(Role.CUSTOMER);
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setPassword(encoder.encode(body.getPassword()));
        repo.save(user);
    }

    public String verify(LoginBody user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            return "fail";
        }
    }
}

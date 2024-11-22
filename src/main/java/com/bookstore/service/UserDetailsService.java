package com.bookstore.service;

import com.bookstore.model.User;
import com.bookstore.model.UserPrincipal;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class that implements Spring Security's UserDetailsService
 * to load user-specific data for authentication.
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    /**
     * Loads a user's details by their username for authentication purposes.
     * Throws an exception if the user is not found.
     *
     * @param username the username identifying the user whose data is required
     * @return UserDetails object containing the user's security information
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        
        return new UserPrincipal(user.get());
    }
}

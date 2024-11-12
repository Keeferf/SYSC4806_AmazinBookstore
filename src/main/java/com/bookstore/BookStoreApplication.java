package com.bookstore;

import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

    private static final Logger log = LoggerFactory.getLogger(BookStoreApplication.class);

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    /**
     * Initializes the default users (customer and admin) in the database.
     * If no users exist, creates a customer and an admin user.
     */
    @Bean
    public CommandLineRunner initializeUsers() {
        return args -> {

            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                // Create customer user
                User customer = new User("customer", Role.CUSTOMER);
                userRepository.save(customer);
                log.info("Created customer user with ID: {}", customer.getId());

                // Create admin user
                User admin = new User("admin", Role.ADMIN);
                userRepository.save(admin);
                log.info("Created admin user with ID: {}", admin.getId());

                log.info("User initialization completed");
            } else {
                log.info("Users already exist");
            }
        };
    }
}
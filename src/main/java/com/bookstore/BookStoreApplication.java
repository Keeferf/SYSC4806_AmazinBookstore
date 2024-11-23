package com.bookstore;

import com.bookstore.dto.RegistrationDTO;
import com.bookstore.model.Role;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.UserService;
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

    @Autowired
    private UserService userService;

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
            //userRepository.deleteAll();  //may need to uncomment this on first run to allow creation of users
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                // Create customer user
                RegistrationDTO customer = new RegistrationDTO("customer", "pass", "customer@email.com",
                        "CustomerFirstName", "lastName");
                userService.register(customer, Role.CUSTOMER);
                log.info("Created customer user with ID: {}", customer.getUsername());

                // Create admin user
                RegistrationDTO admin = new RegistrationDTO("admin", "admin123", "admin@email.com",
                        "AdminFirst", "AdminLast");
                userService.register(admin, Role.ADMIN);
                log.info("Created admin user with Username: {}", admin.getUsername());

                log.info("User initialization completed");
            } else {
                log.info("Users already exist");
            }
        };
    }
}
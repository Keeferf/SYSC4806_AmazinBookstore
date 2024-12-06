package com.bookstore;

import com.bookstore.dto.RegistrationDTO;
import com.bookstore.model.Role;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.UserService;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Starts up the application
 */
@SpringBootApplication
public class BookStoreApplication {

    private static final Logger log = LoggerFactory.getLogger(BookStoreApplication.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
        MongoConnection();
    }

    

    /**
     * Connects to the MongoDB
     */
    public static void MongoConnection() {
        // Comment out when wanting to run locally or through Azure
        String connectionString = System.getenv("MONGODB_URI");
        // String connectionString = "mongodb+srv://DBAccess:SYSC4806pass@cluster0.ag8xa.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        
        if (connectionString == null || connectionString.isEmpty()) {
            log.warn("MONGODB_URI environment variable is not set. Falling back to hardcoded connection string for local testing.");
    
        }
    
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
    
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
    
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("bookstoreDB");
            database.runCommand(new Document("ping", 1));
            log.info("Pinged your deployment. Successfully connected to MongoDB!");
        } catch (MongoException e) {
            log.error("Failed to connect to MongoDB: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    


    /**
     * Initializes the default users (customer and admin) in the database.
     * If no users exist, creates a customer and an admin user.
     */
    @Bean
    public CommandLineRunner initializeUsers() {
        return args -> {
            // userRepository.deleteAll();  // Uncomment this on first run if needed
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                // create admin user
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

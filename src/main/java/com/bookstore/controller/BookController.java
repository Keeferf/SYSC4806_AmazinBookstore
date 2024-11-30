package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.PurchaseItem;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CheckoutRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for managing books and handling book-related operations in the bookstore.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    /**
     * Retrieves all books from the database.
     */
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Finds a specific book by its unique identifier.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        return bookRepository.findById(id)
                .map(book -> ResponseEntity.ok().body(book))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Searches for books by matching title keywords.
     */
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * Searches for books by ISBN pattern.
     */
    @GetMapping("/search/isbn")
    public ResponseEntity<List<Book>> searchBooksByIsbn(@RequestParam String isbn) {
        List<Book> books = bookRepository.findByIsbnContainingIgnoreCase(isbn);
        return ResponseEntity.ok(books);
    }

    /**
     * Searches for books by author name.
     */
    @GetMapping("/search/author")
    public List<Book> searchBooksByAuthor(@RequestParam String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Searches for books by publisher name.
     */
    @GetMapping("/search/publisher")
    public List<Book> searchBooksByPublisher(@RequestParam String publisher) {
        return bookRepository.findByPublisherContainingIgnoreCase(publisher);
    }

    /**
     * Filters books within a specified price range.
     */
    @GetMapping("/filter/price")
    public List<Book> filterBooksByPrice(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Filters books by minimum inventory level.
     */
    @GetMapping("/filter/inventory")
    public List<Book> filterBooksByInventory(@RequestParam int minInventory) {
        return bookRepository.findByInventoryGreaterThan(minInventory);
    }

    /**
     * Adds a new book to the inventory (admin access required).
     */
    @PostMapping
    public ResponseEntity<?> uploadBook(@RequestBody Book book) {
        // get current user from Security Context
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Admin role required.");

        try {
            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(savedBook);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toLowerCase().contains("isbn")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A book with ISBN " + book.getIsbn() + " already exists.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error saving book: " + e.getMessage());
        }
    }

    /**
     * Updates an existing book's information (admin access required).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editBook(@PathVariable String id, @RequestBody Book bookDetails) {
        // get current user from Security Context
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Admin role required.");

        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook == null) {
            return ResponseEntity.notFound().build();
        }

        // only check ISBN if it's being changed
        if (!existingBook.getIsbn().equals(bookDetails.getIsbn())) {
            List<Book> booksWithSimilarIsbn = bookRepository.findByIsbnContainingIgnoreCase(bookDetails.getIsbn());
            // check for exact match within the similar results
            boolean exactMatchExists = booksWithSimilarIsbn.stream()
                    .anyMatch(book -> book.getIsbn().equalsIgnoreCase(bookDetails.getIsbn()) &&
                            book.getId() != existingBook.getId());

            if (exactMatchExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A book with ISBN " + bookDetails.getIsbn() + " already exists.");
            }
        }

        try {
            existingBook.setIsbn(bookDetails.getIsbn());
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setDescription(bookDetails.getDescription());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setPublisher(bookDetails.getPublisher());
            existingBook.setPictureURL(bookDetails.getPictureURL());
            existingBook.setPrice(bookDetails.getPrice());
            existingBook.setInventory(bookDetails.getInventory());

            Book updatedBook = bookRepository.save(existingBook);
            return ResponseEntity.ok(updatedBook);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toLowerCase().contains("isbn")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A book with ISBN " + bookDetails.getIsbn() + " already exists.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating book: " + e.getMessage());
        }
    }

    /**
     * Removes a book from the inventory (admin access required).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Admin role required.");
        return bookRepository.findById(id).map(book -> {
            bookRepository.delete(book);
            return ResponseEntity.ok().body("Book deleted successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns recommended books for a user based on Jaccard similarity of purchase history
     */
    @GetMapping("/recommended")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Book>> getRecommendedBooks(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> userBooks = getUserPurchasedBooks(user);

        if (userBooks.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // get all users and their purchased books
        Map<String, Set<String>> userPurchaseMap = getAllUserPurchases();

        // find most similar user using Jaccard similarity
        String mostSimilarUserId = findMostSimilarUser(user.getId(), userBooks, userPurchaseMap);

        if (mostSimilarUserId == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // get recommended books for the current user
        List<Book> recommendations = getRecommendedBooksForUser(userBooks, userPurchaseMap.get(mostSimilarUserId));

        return ResponseEntity.ok(recommendations);
    }

    private Set<String> getUserPurchasedBooks(User user) {
        return checkoutRepository.findByUserId(user.getId()).stream()
                .flatMap(checkout -> checkout.getItems().stream())
                .map(PurchaseItem::getBookId) // get book IDs
                .collect(Collectors.toSet());
    }

    private Map<String, Set<String>> getAllUserPurchases() {
        Map<String, Set<String>> purchaseMap = new HashMap<>();

        List<User> users = userRepository.findAll();
        for (User user : users) {
            Set<String> userBooks = getUserPurchasedBooks(user);
            if (!userBooks.isEmpty()) {
                purchaseMap.put(user.getId(), userBooks);
            }
        }

        return purchaseMap;
    }

    /**
     * Finds the most similar user to the current user based on Jaccard similarity of purchase history.
     */
    private String findMostSimilarUser(String currentUserId, Set<String> userBooks,
                                     Map<String, Set<String>> userPurchaseMap) {
        double maxSimilarity = 0.0;
        String mostSimilarUserId = null;

        for (Map.Entry<String, Set<String>> entry : userPurchaseMap.entrySet()) {
            if (!entry.getKey().equals(currentUserId)) {
                double similarity = calculateJaccardSimilarity(userBooks, entry.getValue());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarUserId = entry.getKey();
                }
            }
        }

        return mostSimilarUserId;
    }

    /**
     * Calculates the Jaccard similarity between two sets.
     */
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Gets the recommended books for the current user based on the most similar user's purchase history.
     */
    private List<Book> getRecommendedBooksForUser(Set<String> userBooks, Set<String> similarUserBooks) {
        Set<String> recommendedBookIds = new HashSet<>(similarUserBooks);
        recommendedBookIds.removeAll(userBooks);

        return bookRepository.findAllById(recommendedBookIds);
    }


    /**
     * Checks if the current user has admin role.
     */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Role.ADMIN.equals(user.getRole());
    }


}
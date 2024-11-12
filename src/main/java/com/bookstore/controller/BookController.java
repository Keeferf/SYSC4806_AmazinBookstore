package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
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
    public ResponseEntity<?> uploadBook(@RequestBody Book book, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        try {
            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving book: " + e.getMessage());
        }
    }

    /**
     * Updates an existing book's information (admin access required).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editBook(@PathVariable Long id, @RequestBody Book bookDetails, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        return bookRepository.findById(id).map(book -> {
            book.setIsbn(bookDetails.getIsbn());
            book.setTitle(bookDetails.getTitle());
            book.setDescription(bookDetails.getDescription());
            book.setAuthor(bookDetails.getAuthor());
            book.setPublisher(bookDetails.getPublisher());
            book.setPictureURL(bookDetails.getPictureURL());
            book.setPrice(bookDetails.getPrice());
            book.setInventory(bookDetails.getInventory());
            bookRepository.save(book);
            return ResponseEntity.ok(book);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Removes a book from the inventory (admin access required).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        return bookRepository.findById(id).map(book -> {
            bookRepository.delete(book);
            return ResponseEntity.ok().body("Book deleted successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
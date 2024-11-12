package com.bookstore.controller;

import com.bookstore.dto.CartItemDTO;
import com.bookstore.model.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CheckoutRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling purchase-related API endpoints.
 */
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private CheckoutRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Handles the checkout process.
     * Deducts purchased quantities from inventory and creates purchase records.
     *
     * @param userId    The ID of the user making the purchase.
     * @param cartItems The list of items in the cart.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<String> checkout(@RequestParam Long userId, @RequestBody List<CartItemDTO> cartItems) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.CUSTOMER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found or invalid role.");
        }

        List<PurchaseItem> purchaseItems = new ArrayList<>();

        for (CartItemDTO cartItemDTO : cartItems) {
            Optional<Book> optionalBook = bookRepository.findById(cartItemDTO.getBookId());
            if (optionalBook.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Book with ID " + cartItemDTO.getBookId() + " not found.");
            }

            Book book = optionalBook.get();

            // check if enough inventory is available
            if (book.getInventory() < cartItemDTO.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Not enough inventory for book: " + book.getTitle());
            }

            // deduct the purchased quantity from the inventory
            book.setInventory(book.getInventory() - cartItemDTO.getQuantity());
            bookRepository.save(book);

            // create PurchaseItem with book details
            PurchaseItem purchaseItem = new PurchaseItem(book, cartItemDTO.getQuantity(), null);
            purchaseItems.add(purchaseItem);
        }

        // create a new checkout record
        Checkout purchase = new Checkout();
        purchase.setUser(user);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setItems(purchaseItems);

        // associate purchase items with the checkout
        for (PurchaseItem item : purchaseItems) {
            item.setPurchase(purchase);
        }

        purchaseRepository.save(purchase);

        return ResponseEntity.ok("Checkout successful.");
    }

    /**
     * Placeholder for recommendations endpoint.
     *
     * @param userId The ID of the user requesting recommendations.
     * @return A list of recommended books.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<Book>> getRecommendations(@RequestParam Long userId) {
        // Implement recommendation logic here
        return ResponseEntity.ok(new ArrayList<>()); // Placeholder
    }
}

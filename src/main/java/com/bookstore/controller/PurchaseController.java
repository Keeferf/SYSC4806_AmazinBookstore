package com.bookstore.controller;

import com.bookstore.dto.CartItemDTO;
import com.bookstore.dto.PurchaseDTO;
import com.bookstore.model.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CheckoutRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param cartItems The list of items in the cart
     * @param principal The authentication principal
     * @return ResponseEntity with status and message
     */
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> checkout(@RequestBody List<CartItemDTO> cartItems, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Create and save checkout first to get an ID
            Checkout checkout = new Checkout(user);
            checkout = purchaseRepository.save(checkout);

            List<PurchaseItem> purchaseItems = new ArrayList<>();

            for (CartItemDTO cartItemDTO : cartItems) {
                Book book = bookRepository.findById(cartItemDTO.getBookId())
                        .orElseThrow(() -> new RuntimeException("Book not found: " + cartItemDTO.getBookId()));

                if (book.getInventory() < cartItemDTO.getQuantity()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Not enough inventory for book: " + book.getTitle());
                }

                // Update inventory
                book.setInventory(book.getInventory() - cartItemDTO.getQuantity());
                bookRepository.save(book);

                // Create purchase item with just the ID reference
                PurchaseItem purchaseItem = new PurchaseItem(book, cartItemDTO.getQuantity(), checkout);
                purchaseItems.add(purchaseItem);
            }

            // Update checkout with items
            checkout.setItems(purchaseItems);
            purchaseRepository.save(checkout);

            return ResponseEntity.ok("Checkout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Checkout failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves the purchase history for the authenticated user.
     * Fetches all checkout records associated with the user and converts them to DTOs
     * containing purchase details like ID, date, and items purchased.
     *
     * @param principal The authenticated user's principal containing user details
     * @return ResponseEntity containing a list of PurchaseDTO objects representing the user's purchase history
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PurchaseDTO>> getPurchaseHistory(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            List<Checkout> checkouts = purchaseRepository.findByUserId(user.getId());
            List<PurchaseDTO> purchaseHistory = new ArrayList<>();

            for (Checkout checkout : checkouts) {
                List<CartItemDTO> items = checkout.getItems().stream()
                        .map(item -> new CartItemDTO(
                                item.getBookId(),
                                item.getQuantity(),
                                item.getTitle(),
                                item.getAuthor(),
                                item.getIsbn(),
                                item.getPurchasePrice()
                        ))
                        .collect(Collectors.toList());

                purchaseHistory.add(new PurchaseDTO(
                        checkout.getId(),
                        checkout.getPurchaseDate(),
                        items
                ));
            }

            return ResponseEntity.ok(purchaseHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
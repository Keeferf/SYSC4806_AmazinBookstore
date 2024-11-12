package com.bookstore.controller;

import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling shopping cart operations for the bookstore.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    /**
     * Adds an item to the user's shopping cart.
     * @return ResponseEntity with success message or error details
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItem cartItem, @RequestParam Long userId) {
        try {
            if (cartItem == null || userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cart item and user ID cannot be null");
            }

            Cart cart = cartRepository.findById(userId)
                    .orElse(new Cart());
            cart.getItems().add(cartItem);
            cartRepository.save(cart);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Item successfully added to cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add item to cart: " + e.getMessage());
        }
    }

    /**
     * Removes an item from the user's shopping cart.
     * @return ResponseEntity with success message or error details
     */
    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestBody CartItem cartItem, @RequestParam Long userId) {
        try {
            if (cartItem == null || userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cart item and user ID cannot be null");
            }

            Cart cart = cartRepository.findById(userId)
                    .orElse(new Cart());

            if (!cart.getItems().contains(cartItem)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Item not found in cart");
            }

            cart.getItems().remove(cartItem);
            cartRepository.save(cart);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Item successfully removed from cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove item from cart: " + e.getMessage());
        }
    }

    /**
     * Removes all items from the user's shopping cart.
     * @return ResponseEntity with success message or error details
     */
    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User ID cannot be null");
            }

            Cart cart = cartRepository.findById(userId)
                    .orElse(new Cart());
            cart.getItems().clear();
            cartRepository.save(cart);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Cart successfully cleared");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to clear cart: " + e.getMessage());
        }
    }
}
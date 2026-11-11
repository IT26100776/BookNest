
package com.booknest.demo.shoppingcart.controller;

import com.booknest.demo.shoppingcart.Cart;
import com.booknest.demo.shoppingcart.dto.CartItemRequest;
import com.booknest.demo.shoppingcart.service.CartService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. Get customer's cart
    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable String customerId)
            throws IOException {

        return ResponseEntity.ok(
                cartService.getCart(customerId)
        );
    }

    // 2. Add book to cart
    @PostMapping("/{customerId}/items")
    public ResponseEntity<Cart> addItem(
            @PathVariable String customerId,
            @RequestBody CartItemRequest request)
            throws IOException {

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        Cart cart = cartService.addItem(
                customerId,
                request.getBookId(),
                request.getQuantity()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cart);
    }

    // 3. Update book quantity
    @PutMapping("/{customerId}/items/{bookId}")
    public ResponseEntity<Cart> updateItem(
            @PathVariable String customerId,
            @PathVariable int bookId,
            @RequestBody CartItemRequest request)
            throws IOException {

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        Cart cart = cartService.updateItem(
                customerId,
                bookId,
                request.getQuantity()
        );

        return ResponseEntity.ok(cart);
    }

    // 4. Remove book from cart
    @DeleteMapping("/{customerId}/items/{bookId}")
    public ResponseEntity<Cart> removeItem(
            @PathVariable String customerId,
            @PathVariable int bookId)
            throws IOException {

        Cart cart = cartService.removeItem(
                customerId,
                bookId
        );

        return ResponseEntity.ok(cart);
    }

    // 5. Clear entire cart
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable String customerId)
            throws IOException {

        cartService.clearCart(customerId);

        return ResponseEntity.noContent().build();
    }

    // 6. Get total quantity
    @GetMapping("/{customerId}/count")
    public ResponseEntity<Map<String, Integer>> getTotalItems(
            @PathVariable String customerId)
            throws IOException {

        int total = cartService.getTotalItems(customerId);

        return ResponseEntity.ok(
                Map.of("totalItems", total)
        );
    }
}


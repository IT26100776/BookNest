
package com.booknest.demo.shoppingcart.service;

import com.booknest.demo.book.BookService;
import com.booknest.demo.shoppingcart.Cart;
import com.booknest.demo.shoppingcart.CartFileManager;
import com.booknest.demo.shoppingcart.CartItem;
import com.booknest.demo.book.Book;

import org.springframework.stereotype.Service;
import com.booknest.demo.shoppingcart.CartFileManager;

import java.io.IOException;
import java.util.List;

@Service
public class CartService {

    private final BookService bookService;
    private final CartFileManager cartFileManager =
            new CartFileManager();

    public CartService(BookService bookService) {
        this.bookService = bookService;
    }

    // Get customer's cart
    public Cart getCart(String customerId)
            throws IOException {

        validateCustomerId(customerId);

        return new Cart(customerId);
    }

    // Add a book to cart
    public Cart addItem(
            String customerId,
            int bookId,
            int quantity) throws IOException {

        validateCustomerId(customerId);

        if (bookId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Book ID and quantity must be positive."
            );
        }

        // Check whether the book exists
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book with ID " + bookId
                                        + " was not found."
                        )
                );

// Load the customer's existing cart
        Cart cart = new Cart(customerId);

// Find the existing quantity of this book
        int currentQuantity = cart.getItems().stream()
                .filter(item -> item.getBookId() == bookId)
                .mapToInt(CartItem::getQuantity)
                .findFirst()
                .orElse(0);

// Validate available stock
        if (currentQuantity + quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for: " + book.getTitle()
            );
        }

// Add the book
        cart.addItem(bookId, quantity);

        return cart;
    }

    // Update book quantity
    public Cart updateItem(
            String customerId,
            int bookId,
            int quantity) throws IOException {

        validateCustomerId(customerId);

        // Check whether the book exists
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book with ID " + bookId + " was not found."
                        )
                );

        // Validate requested quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        // Check available stock
        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for: " + book.getTitle()
            );
        }

        // Load customer's cart
        Cart cart = new Cart(customerId);

        // Update quantity
        cart.updateQuantity(bookId, quantity);

        return cart;
    }


    // Remove a book from cart
    public Cart removeItem(
            String customerId,
            int bookId) throws IOException {

        validateCustomerId(customerId);

        Cart cart = new Cart(customerId);

        cart.removeItem(bookId);

        return cart;
    }

    // Clear customer's cart
    // Clear customer's cart
    public void clearCart(String customerId)
            throws IOException {

        validateCustomerId(customerId);

        Cart cart = new Cart(customerId);

        cart.clearCart();
    }

    // Get total number of books in cart
    public int getTotalItems(String customerId)
            throws IOException {

        Cart cart = getCart(customerId);

        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Validate customer ID
    private void validateCustomerId(String customerId) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be empty."
            );
        }
    }
}


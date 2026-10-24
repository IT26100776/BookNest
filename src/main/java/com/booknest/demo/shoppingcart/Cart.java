
package com.booknest.demo.shoppingcart;

import java.io.IOException;
import java.util.List;

public class Cart {

    private int customerId;
    private List<CartItem> items;
    private CartFileManager fileManager;

    public Cart(int customerId) throws IOException {

        this.customerId = customerId;

        this.fileManager = new CartFileManager();

        // Load previously saved cart items
        this.items = fileManager.loadCart(customerId);
    }

    public int getCustomerId() {
        return customerId;
    }

    // Get cart items
    public List<CartItem> getItems() {
        return items;
    }

    // Add item to cart
    public void addItem(int bookId, int quantity) throws IOException {

        if (bookId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Book ID and quantity must be positive");
        }

        for (CartItem item : items) {

            if (item.getBookId() == bookId) {

                item.setQuantity(item.getQuantity() + quantity);

                fileManager.saveCart(customerId, items);
                return;
            }
        }

        items.add(new CartItem(bookId, quantity));

        fileManager.saveCart(customerId, items);
    }

    // Remove item from cart
    public void removeItem(int bookId) throws IOException {

        items.removeIf(item -> item.getBookId() == bookId);

        fileManager.saveCart(customerId, items);
    }

    // Update item quantity
    public void updateQuantity(int bookId, int quantity)
            throws IOException {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        for (CartItem item : items) {

            if (item.getBookId() == bookId) {

                item.setQuantity(quantity);

                fileManager.saveCart(customerId, items);
                return;
            }
        }
    }

    // Clear entire cart
    public void clearCart() throws IOException {

        items.clear();

        fileManager.saveCart(customerId, items);
    }
}

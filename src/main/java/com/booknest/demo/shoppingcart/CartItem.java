
package com.booknest.demo.shoppingcart;

public class CartItem {

    private int bookId;
    private int quantity;

    // Default constructor
    public CartItem() {
    }

    // Parameterized constructor
    public CartItem(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters
    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        this.quantity = quantity;
    }
}

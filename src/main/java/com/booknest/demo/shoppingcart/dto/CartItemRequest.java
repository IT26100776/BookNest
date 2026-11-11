
package com.booknest.demo.shoppingcart.dto;

public class CartItemRequest {

    private int bookId;
    private int quantity;

    public CartItemRequest() {
    }

    public CartItemRequest(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

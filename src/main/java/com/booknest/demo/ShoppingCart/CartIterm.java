package com.booknest.demo.ShoppingCart;

public class CartIterm{

    private int bookId;
    private int quantity;

    public CartIterm(int bookId, int quantity){
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

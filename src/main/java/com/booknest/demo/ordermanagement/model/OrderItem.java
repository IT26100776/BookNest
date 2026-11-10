
package com.booknest.demo.ordermanagement.model;

import java.math.BigDecimal;

public class OrderItem {

    private int bookId;
    private String bookTitle;
    private int quantity;
    private BigDecimal unitPrice;

    // Default constructor
    public OrderItem() {
    }

    // Parameterized constructor
    public OrderItem(int bookId, String bookTitle,
                     int quantity, BigDecimal unitPrice) {

        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Calculate subtotal
    public BigDecimal getSubtotal() {

        return unitPrice.multiply(BigDecimal.valueOf(quantity));

    }

    // Getters and Setters

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

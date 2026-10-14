
package com.booknest.demo.book;

import java.math.BigDecimal;

public class Book {

    private int id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private int stockQuantity;
    private int pages;
    private String publisher;
    private String genre;
    private String language;

    public Book() {
    }

    public Book(int id, String title, String author,
                String isbn, BigDecimal price, int stockQuantity, int pages, String publisher, String genre, String language) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.pages = pages;
        this.publisher = publisher;
        this.genre = genre;
        this.language = language;
    }

    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public String getPublisher(){
        return publisher;
    }

    public String getGenre(){
        return genre;
    }

    public String getLanguage(){
        return  language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public void setPages(int pages){
        this.pages = pages;
    }
    public int getPages(){
        return pages;
    }
}

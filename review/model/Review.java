package com.booknest.demo.review.model;

public class Review {
    private String id;
    private String bookId;
    private String reviewerName;
    private int rating;
    private String comment;

    public Review() {
    }

    public Review(String id, String bookId, String reviewerName,
                  int rating, String comment) {
        this.id = id;
        this.bookId = bookId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewType() {
        return "Public Review";
    }
}

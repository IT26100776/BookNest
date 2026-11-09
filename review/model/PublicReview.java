package com.booknest.demo.review.model;

public class PublicReview extends Review {
    public PublicReview() {
        super();
    }

    public PublicReview(String id, String bookId, String reviewerName,
                        int rating, String comment) {
        super(id, bookId, reviewerName, rating, comment);
    }

    @Override
    public String getReviewType() {
        return "Public Review";
    }
}

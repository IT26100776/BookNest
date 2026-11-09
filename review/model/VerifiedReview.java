package com.booknest.demo.review.model;

public class VerifiedReview extends Review {
    public VerifiedReview() {
        super();
    }

    public VerifiedReview(String id, String bookId, String reviewerName,
                          int rating, String comment) {
        super(id, bookId, reviewerName, rating, comment);
    }

    @Override
    public String getReviewType() {
        return "Verified Review";
    }

}

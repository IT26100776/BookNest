package com.booknest.demo.review.service;
import com.booknest.demo.review.model.PublicReview;
import com.booknest.demo.review.model.Review;
import com.booknest.demo.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class ReviewService {
    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public List<Review> getAllReviews() {
        return repository.findAll();
    }

    public List<Review> getReviewsByBook(String bookId) {
        return repository.findAll().stream()
                .filter(r -> r.getBookId().equalsIgnoreCase(bookId))
                .collect(java.util.stream.Collectors.toList());
    }


    public Review addReview(Review review) {
        validate(review);

        List<Review> reviews = repository.findAll();

        review.setId(UUID.randomUUID().toString());

        reviews.add(new PublicReview(
                review.getId(),
                review.getBookId().trim(),
                review.getReviewerName().trim(),
                review.getRating(),
                review.getComment().trim()
        ));

        repository.saveAll(reviews);

        return reviews.get(reviews.size() - 1);
    }

    public Review updateReview(String id, Review updatedReview) {
        validate(updatedReview);

        List<Review> reviews = repository.findAll();

        for (int i = 0; i < reviews.size(); i++) {
            Review existing = reviews.get(i);

            if (existing.getId().equals(id)) {
                Review updated = new PublicReview(
                        id,
                        updatedReview.getBookId().trim(),
                        updatedReview.getReviewerName().trim(),
                        updatedReview.getRating(),
                        updatedReview.getComment().trim()
                );

                reviews.set(i, updated);
                repository.saveAll(reviews);

                return updated;
            }
        }

        throw new IllegalArgumentException("Review not found.");
    }

    public void deleteReview(String id) {
        List<Review> reviews = repository.findAll();

        boolean removed = reviews.removeIf(
                review -> review.getId().equals(id));

        if (!removed) {
            throw new IllegalArgumentException("Review not found.");
        }

        repository.saveAll(reviews);
    }

    private void validate(Review review) {
        if (review == null
                || review.getBookId() == null
                || review.getReviewerName() == null
                || review.getComment() == null) {
            throw new IllegalArgumentException(
                    "All review fields are required.");
        }

        if (review.getBookId().isBlank()
                || review.getReviewerName().isBlank()
                || review.getComment().isBlank()) {
            throw new IllegalArgumentException(
                    "Fields cannot be empty.");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }
    }
}

package com.booknest.demo.review.controller;
import com.booknest.demo.review.model.Review;
import com.booknest.demo.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")

public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return service.getAllReviews();
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getReviewsByBook(
            @PathVariable String bookId) {
        return service.getReviewsByBook(bookId);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            Review saved = service.addReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable String id,
            @RequestBody Review review) {
        try {
            return ResponseEntity.ok(service.updateReview(id, review));
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().equals("Review not found.")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        try {
            service.deleteReview(id);
            return ResponseEntity.ok(
                    Map.of("message", "Review deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

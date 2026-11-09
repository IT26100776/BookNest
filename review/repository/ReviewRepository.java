package com.booknest.demo.review.repository;

import com.booknest.demo.review.model.PublicReview;
import com.booknest.demo.review.model.Review;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Repository
public class ReviewRepository {
    private final Path filePath =
            Paths.get("data", "reviews.txt").toAbsolutePath();

    public ReviewRepository() {
        try {
            Files.createDirectories(filePath.getParent());

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create reviews file.", e);
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8);
    }

    public synchronized List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();

        try {
            for (String line :
                    Files.readAllLines(filePath, StandardCharsets.UTF_8)) {

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                if (parts.length != 5) {
                    continue;
                }

                try {
                    reviews.add(new PublicReview(
                            parts[0],
                            decode(parts[1]),
                            decode(parts[2]),
                            Integer.parseInt(parts[3]),
                            decode(parts[4])
                    ));
                } catch (IllegalArgumentException e) {
                    // Skip malformed records.
                }
            }

            return reviews;

        } catch (IOException e) {
            throw new RuntimeException("Could not read reviews.", e);
        }
    }

    public synchronized void saveAll(List<Review> reviews) {
        List<String> lines = new ArrayList<>();

        for (Review review : reviews) {
            lines.add(
                    review.getId() + "|" +
                            encode(review.getBookId()) + "|" +
                            encode(review.getReviewerName()) + "|" +
                            review.getRating() + "|" +
                            encode(review.getComment())
            );
        }

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not save reviews.", e);
        }
    }

}

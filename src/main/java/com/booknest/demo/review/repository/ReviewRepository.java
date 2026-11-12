
package com.booknest.demo.review.repository;

import com.booknest.demo.review.model.VerifiedReview;
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

    // Escape special characters for readable text storage
    private String escape(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    // Restore escaped text
    private String unescape(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);

                switch (next) {
                    case '\\' -> result.append('\\');
                    case 'p' -> result.append('|');
                    case 'r' -> result.append('\r');
                    case 'n' -> result.append('\n');
                    default -> result.append('\\').append(next);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // Read old and new review records
    public synchronized List<Review> findAll() {

        List<Review> reviews = new ArrayList<>();

        try {
            for (String line :
                    Files.readAllLines(filePath, StandardCharsets.UTF_8)) {

                if (line.isBlank()) continue;

                String[] parts = line.split("\\|", -1);

                try {
                    Review review;

                    // New readable format
                    if (parts.length == 7 && parts[0].equals("R2")) {

                        String type = parts[2];

                        if (type.equals("VERIFIED")) {
                            review = new VerifiedReview(
                                    parts[1],
                                    unescape(parts[3]),
                                    unescape(parts[4]),
                                    Integer.parseInt(parts[5]),
                                    unescape(parts[6])
                            );
                        } else if (type.equals("PUBLIC")) {
                            review = new PublicReview(
                                    parts[1],
                                    unescape(parts[3]),
                                    unescape(parts[4]),
                                    Integer.parseInt(parts[5]),
                                    unescape(parts[6])
                            );
                        } else {
                            continue;
                        }
                    }

                    // Old 5-field format
                    else if (parts.length == 5) {
                        review = new PublicReview(
                                parts[0],
                                decode(parts[1]),
                                decode(parts[2]),
                                Integer.parseInt(parts[3]),
                                decode(parts[4])
                        );
                    }

                    // Old 6-field Base64 format
                    else if (parts.length == 6) {

                        String type = parts[1];

                        if (type.equals("VERIFIED")) {
                            review = new VerifiedReview(
                                    parts[0],
                                    decode(parts[2]),
                                    decode(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    decode(parts[5])
                            );
                        } else if (type.equals("PUBLIC")) {
                            review = new PublicReview(
                                    parts[0],
                                    decode(parts[2]),
                                    decode(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    decode(parts[5])
                            );
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }

                    reviews.add(review);

                } catch (IllegalArgumentException e) {
                    // Skip malformed records
                }
            }

            return reviews;

        } catch (IOException e) {
            throw new RuntimeException("Could not read reviews.", e);
        }
    }

    // Decode existing Base64 records
    private String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // Save reviews in readable format
    public synchronized void saveAll(List<Review> reviews) {

        List<String> lines = new ArrayList<>();

        for (Review review : reviews) {

            String type = review instanceof VerifiedReview
                    ? "VERIFIED"
                    : "PUBLIC";

            lines.add(
                    "R2|" +
                            review.getId() + "|" +
                            type + "|" +
                            escape(review.getBookId()) + "|" +
                            escape(review.getReviewerName()) + "|" +
                            review.getRating() + "|" +
                            escape(review.getComment())
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

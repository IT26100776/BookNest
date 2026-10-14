
package com.booknest.demo.book;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class BookFileStorage {

    private static final Path FILE_PATH =
            Path.of("data", "books.txt");

    // Load all books from the text file
    public List<Book> loadBooks() {

        List<Book> books = new ArrayList<>();

        try {
            if (!Files.exists(FILE_PATH)) {
                return books;
            }

            List<String> lines =
                    Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 10) {
                    throw new IllegalStateException(
                            "Invalid book record: " + line);
                }

                Book book = new Book(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3],
                        new BigDecimal(data[4]),
                        Integer.parseInt(data[5]),
                        Integer.parseInt(data[6]),
                        data[7],
                        data[8],
                        data[9]
                );

                books.add(book);
            }

            return books;

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Could not read books from file.", e);
        }
    }

    // Save all books to the text file
    public void saveBooks(List<Book> books) {

        List<String> lines = new ArrayList<>();

        for (Book book : books) {

            String line = String.join("|",
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPrice().toString(),
                    String.valueOf(book.getStockQuantity()),
                    String.valueOf(book.getPages()),
                    book.getPublisher(),
                    book.getGenre(),
                    book.getLanguage()
            );

            lines.add(line);
        }

        try {
            Files.createDirectories(FILE_PATH.getParent());

            Files.write(
                    FILE_PATH,
                    lines,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Could not save books to file.", e);
        }
    }
}

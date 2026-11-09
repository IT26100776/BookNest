
package com.booknest.demo.shoppingcart;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CartFileManager {

    private final Path filePath =
            Path.of("data", "carts.txt");

    // Load cart items for a customer
    public synchronized List<CartItem> loadCart(
            String customerId) throws IOException {

        List<CartItem> items = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return items;
        }

        List<String> lines = Files.readAllLines(
                filePath, StandardCharsets.UTF_8
        );

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }

            String[] data = line.split("\\|", -1);

            if (data.length != 3) {
                continue;
            }

            try {
                String savedCustomerId = data[0];
                int bookId = Integer.parseInt(data[1]);
                int quantity = Integer.parseInt(data[2]);

                if (savedCustomerId.equals(customerId)
                        && bookId > 0 && quantity > 0) {

                    items.add(new CartItem(bookId, quantity));
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid cart record: " + line
                );
            }
        }

        return items;
    }

    // Save cart items for a customer
    public synchronized void saveCart(
            String customerId,
            List<CartItem> items) throws IOException {

        if (customerId == null || customerId.isBlank()
                || customerId.contains("|")
                || customerId.contains("\n")
                || customerId.contains("\r")) {
            throw new IllegalArgumentException(
                    "Invalid customer ID."
            );
        }

        Files.createDirectories(filePath.getParent());

        List<String> lines = new ArrayList<>();

        if (Files.exists(filePath)) {

            for (String line : Files.readAllLines(
                    filePath, StandardCharsets.UTF_8)) {

                String[] data = line.split("\\|", -1);

                if (data.length != 3
                        || !data[0].equals(customerId)) {
                    lines.add(line);
                }
            }
        }

        for (CartItem item : items) {

            lines.add(
                    customerId + "|"
                            + item.getBookId() + "|"
                            + item.getQuantity()
            );
        }

        Path tempFile = Files.createTempFile(
                filePath.getParent(),
                "carts-",
                ".tmp"
        );

        try {
            Files.write(
                    tempFile,
                    lines,
                    StandardCharsets.UTF_8
            );

            Files.move(
                    tempFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}

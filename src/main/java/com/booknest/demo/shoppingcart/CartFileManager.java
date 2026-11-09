
package com.booknest.demo.shoppingcart;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CartFileManager {

    private final Path filePath = Path.of("carts.txt");

    // Load all cart items for a customer
    public List<CartItem> loadCart(int customerId)
            throws IOException {

        List<CartItem> items = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return items;
        }

        List<String> lines = Files.readAllLines(
                filePath, StandardCharsets.UTF_8);

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }

            String[] data = line.split("\\|", -1);

            if (data.length != 3) {
                continue;
            }

            try {
                int savedCustomerId = Integer.parseInt(data[0]);
                int bookId = Integer.parseInt(data[1]);
                int quantity = Integer.parseInt(data[2]);

                if (savedCustomerId == customerId) {
                    items.add(new CartItem(bookId, quantity));
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid cart record: " + line);
            }
        }

        return items;
    }

    // Save cart items for a customer
    public void saveCart(int customerId, List<CartItem> items)
            throws IOException {

        List<String> lines = new ArrayList<>();

        if (Files.exists(filePath)) {
            for (String line : Files.readAllLines(
                    filePath, StandardCharsets.UTF_8)) {

                String[] data = line.split("\\|", -1);

                if (data.length != 3) {
                    lines.add(line);
                    continue;
                }

                try {
                    int savedCustomerId = Integer.parseInt(data[0]);

                    if (savedCustomerId != customerId) {
                        lines.add(line);
                    }

                } catch (NumberFormatException e) {
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

        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}


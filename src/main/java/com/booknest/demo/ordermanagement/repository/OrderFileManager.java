
package com.booknest.demo.ordermanagement.repository;

import org.springframework.stereotype.Repository;

import com.booknest.demo.ordermanagement.model.Order;
import com.booknest.demo.ordermanagement.model.OrderItem;
import com.booknest.demo.ordermanagement.model.OrderStatus;
import com.booknest.demo.ordermanagement.model.PaymentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderFileManager {

    private final Path filePath = Path.of("data", "orders.txt");

    // Escape text so delimiters and line breaks do not damage records
    private String escape(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace(",", "\\c")
                .replace(";", "\\s")
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
                    case 'c' -> result.append(',');
                    case 's' -> result.append(';');
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

    // Load all orders
    public List<Order> loadOrders() throws IOException {

        List<Order> orders = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return orders;
        }

        List<String> lines =
                Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (!line.isBlank()) {
                orders.add(parseOrder(line));
            }
        }

        return orders;
    }

    // Find an order using its ID
    public Optional<Order> findOrderById(int orderId)
            throws IOException {

        return loadOrders().stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst();
    }

    // Find orders belonging to a customer
    public List<Order> findOrdersByCustomerId(String customerId)
            throws IOException {

        return loadOrders().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .toList();
    }

    // Save a new order
    public void saveOrder(Order newOrder) throws IOException {

        if (findOrderById(newOrder.getOrderId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Order ID already exists: " + newOrder.getOrderId()
            );
        }

        List<Order> orders = loadOrders();
        orders.add(newOrder);

        writeOrders(orders);
    }

    // Update an existing order
    public boolean updateOrder(Order updatedOrder)
            throws IOException {

        List<Order> orders = loadOrders();

        for (int i = 0; i < orders.size(); i++) {

            if (orders.get(i).getOrderId()
                    == updatedOrder.getOrderId()) {

                orders.set(i, updatedOrder);
                writeOrders(orders);

                return true;
            }
        }

        return false;
    }

    // Write all orders to the file
    private void writeOrders(List<Order> orders)
            throws IOException {

        Files.createDirectories(filePath.getParent());

        List<String> lines = new ArrayList<>();

        for (Order order : orders) {
            lines.add(formatOrder(order));
        }

        Path tempFile = filePath.resolveSibling("orders.txt.tmp");

        Files.write(
                tempFile,
                lines,
                StandardCharsets.UTF_8
        );

        try {
            Files.move(
                    tempFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(
                    tempFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    // Convert an Order object into a readable text record
    private String formatOrder(Order order) {

        StringBuilder itemData = new StringBuilder();

        for (OrderItem item : order.getItems()) {

            if (!itemData.isEmpty()) {
                itemData.append(";");
            }

            itemData.append(item.getBookId()).append(",")
                    .append(item.getQuantity()).append(",")
                    .append(item.getUnitPrice().toPlainString()).append(",")
                    .append(escape(item.getBookTitle()));
        }

        return "O2|"
                + order.getOrderId() + "|"
                + escape(order.getCustomerId()) + "|"
                + escape(order.getCustomerName()) + "|"
                + escape(order.getEmail()) + "|"
                + escape(order.getPhone()) + "|"
                + escape(order.getDeliveryAddress()) + "|"
                + order.getStatus().name() + "|"
                + order.getPaymentStatus().name() + "|"
                + order.getOrderDate() + "|"
                + order.getTotalAmount().toPlainString() + "|"
                + itemData;
    }

    // Decode existing Base64 URL-encoded customer fields
    private String decodeField(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // Decode existing Base64 book titles
    private String decodeTitle(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // Convert a text record back into an Order object
    private Order parseOrder(String line) throws IOException {

        try {

            String[] data = line.split("\\|", -1);

            Order order = new Order();

            // New readable O2 format
            if (data.length == 12 && data[0].equals("O2")) {

                order.setOrderId(Integer.parseInt(data[1]));
                order.setCustomerId(unescape(data[2]));
                order.setCustomerName(unescape(data[3]));
                order.setEmail(unescape(data[4]));
                order.setPhone(unescape(data[5]));
                order.setDeliveryAddress(unescape(data[6]));
                order.setStatus(OrderStatus.valueOf(data[7]));
                order.setPaymentStatus(PaymentStatus.valueOf(data[8]));
                order.setOrderDate(LocalDateTime.parse(data[9]));

                parseItems(data[11], order, true);

                BigDecimal storedTotal = new BigDecimal(data[10]);

                if (order.getTotalAmount().compareTo(storedTotal) != 0) {
                    throw new IllegalArgumentException(
                            "Order total does not match its items"
                    );
                }

            } else if (data.length == 7) {

                // Backward compatibility with older order records
                order.setOrderId(Integer.parseInt(data[0]));
                order.setCustomerId(data[1]);
                order.setStatus(OrderStatus.valueOf(data[2]));
                order.setPaymentStatus(PaymentStatus.valueOf(data[3]));
                order.setOrderDate(LocalDateTime.parse(data[4]));

                parseItems(data[6], order, false);

                BigDecimal storedTotal = new BigDecimal(data[5]);

                if (order.getTotalAmount().compareTo(storedTotal) != 0) {
                    throw new IllegalArgumentException(
                            "Order total does not match its items"
                    );
                }

            } else if (data.length == 11) {

                // Backward compatibility with existing guest orders
                order.setOrderId(Integer.parseInt(data[0]));
                order.setCustomerId(data[1]);
                order.setCustomerName(decodeField(data[2]));
                order.setEmail(decodeField(data[3]));
                order.setPhone(decodeField(data[4]));
                order.setDeliveryAddress(decodeField(data[5]));
                order.setStatus(OrderStatus.valueOf(data[6]));
                order.setPaymentStatus(PaymentStatus.valueOf(data[7]));
                order.setOrderDate(LocalDateTime.parse(data[8]));

                parseItems(data[10], order, false);

                BigDecimal storedTotal = new BigDecimal(data[9]);

                if (order.getTotalAmount().compareTo(storedTotal) != 0) {
                    throw new IllegalArgumentException(
                            "Order total does not match its items"
                    );
                }

            } else {
                throw new IllegalArgumentException("Invalid order record");
            }

            return order;

        } catch (RuntimeException e) {

            throw new IOException(
                    "Could not read order record: " + line,
                    e
            );
        }
    }

    // Read order items in both old and new formats
    private void parseItems(
            String itemData,
            Order order,
            boolean readableFormat) {

        List<OrderItem> items = new ArrayList<>();

        if (!itemData.isEmpty()) {

            String[] itemRecords = readableFormat
                    ? splitEscaped(itemData, ';')
                    : itemData.split(";");

            for (String record : itemRecords) {

                String[] fields = readableFormat
                        ? splitEscaped(record, ',')
                        : record.split(",", -1);

                if (fields.length != 4) {
                    throw new IllegalArgumentException("Invalid order item");
                }

                int bookId = Integer.parseInt(fields[0]);
                int quantity = Integer.parseInt(fields[1]);
                BigDecimal unitPrice = new BigDecimal(fields[2]);

                String title = readableFormat
                        ? unescape(fields[3])
                        : decodeTitle(fields[3]);

                items.add(new OrderItem(
                        bookId,
                        title,
                        quantity,
                        unitPrice
                ));
            }
        }

        order.setItems(items);
    }

    // Split on a delimiter while respecting escaped characters
    private String[] splitEscaped(String value, char delimiter) {

        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            if (escaped) {
                current.append('\\').append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == delimiter) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (escaped) {
            current.append('\\');
        }

        parts.add(current.toString());

        return parts.toArray(new String[0]);
    }
}

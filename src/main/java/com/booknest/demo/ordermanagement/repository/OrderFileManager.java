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

    // Convert an Order object into a text record

    private String formatOrder(Order order) {

        StringBuilder itemData = new StringBuilder();

        for (OrderItem item : order.getItems()) {

            if (!itemData.isEmpty()) {
                itemData.append(";");
            }

            String encodedTitle = Base64.getEncoder()
                    .encodeToString(
                            item.getBookTitle().getBytes(StandardCharsets.UTF_8)
                    );

            itemData.append(item.getBookId()).append(",")
                    .append(item.getQuantity()).append(",")
                    .append(item.getUnitPrice().toPlainString()).append(",")
                    .append(encodedTitle);
        }

        return order.getOrderId() + "|"
                + order.getCustomerId() + "|"
                + encodeField(order.getCustomerName()) + "|"
                + encodeField(order.getEmail()) + "|"
                + encodeField(order.getPhone()) + "|"
                + encodeField(order.getDeliveryAddress()) + "|"
                + order.getStatus().name() + "|"
                + order.getPaymentStatus().name() + "|"
                + order.getOrderDate() + "|"
                + order.getTotalAmount().toPlainString() + "|"
                + itemData;
    }

    private String encodeField(String value) {
        if (value == null) {
            return "";
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeField(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }


    // Convert a text record back into an Order object

    private Order parseOrder(String line) throws IOException {

        try {

            String[] data = line.split("\\|", -1);

            Order order = new Order();

            if (data.length == 7) {
                // Backward compatibility with older order records

                order.setOrderId(Integer.parseInt(data[0]));
                order.setCustomerId(data[1]);
                order.setStatus(OrderStatus.valueOf(data[2]));
                order.setPaymentStatus(PaymentStatus.valueOf(data[3]));
                order.setOrderDate(LocalDateTime.parse(data[4]));

                parseItems(data[6], order);

                BigDecimal storedTotal = new BigDecimal(data[5]);

                if (order.getTotalAmount().compareTo(storedTotal) != 0) {
                    throw new IllegalArgumentException(
                            "Order total does not match its items"
                    );
                }

            } else if (data.length == 11) {
                // New order records with guest customer details

                order.setOrderId(Integer.parseInt(data[0]));
                order.setCustomerId(data[1]);
                order.setCustomerName(decodeField(data[2]));
                order.setEmail(decodeField(data[3]));
                order.setPhone(decodeField(data[4]));
                order.setDeliveryAddress(decodeField(data[5]));
                order.setStatus(OrderStatus.valueOf(data[6]));
                order.setPaymentStatus(PaymentStatus.valueOf(data[7]));
                order.setOrderDate(LocalDateTime.parse(data[8]));

                parseItems(data[10], order);

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

    private void parseItems(String itemData, Order order) {

        List<OrderItem> items = new ArrayList<>();

        if (!itemData.isEmpty()) {

            String[] itemRecords = itemData.split(";");

            for (String record : itemRecords) {

                String[] fields = record.split(",", -1);

                if (fields.length != 4) {
                    throw new IllegalArgumentException("Invalid order item");
                }

                int bookId = Integer.parseInt(fields[0]);
                int quantity = Integer.parseInt(fields[1]);
                BigDecimal unitPrice = new BigDecimal(fields[2]);

                String title = new String(
                        Base64.getDecoder().decode(fields[3]),
                        StandardCharsets.UTF_8
                );

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

}

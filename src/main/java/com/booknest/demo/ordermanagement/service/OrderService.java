
package com.booknest.demo.ordermanagement.service;

import com.booknest.demo.book.Book;
import com.booknest.demo.book.BookService;
import com.booknest.demo.ordermanagement.model.Order;
import com.booknest.demo.ordermanagement.model.OrderItem;
import com.booknest.demo.ordermanagement.repository.OrderFileManager;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.booknest.demo.ordermanagement.model.OrderStatus;
import com.booknest.demo.ordermanagement.model.PaymentStatus;

@Service
public class OrderService {

    private final OrderFileManager orderFileManager;
    private final BookService bookService;

    // Constructor injection
    public OrderService(OrderFileManager orderFileManager,
                        BookService bookService) {
        this.orderFileManager = orderFileManager;
        this.bookService = bookService;
    }

    // 1. Create a new order
    public Order createOrder(String customerId,
                             List<OrderItem> requestedItems) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be empty."
            );
        }

        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "An order must contain at least one item."
            );
        }

        List<OrderItem> validatedItems = new ArrayList<>();
        Set<Integer> processedBookIds = new HashSet<>();

        // Validate all requested books before changing stock
        for (OrderItem requestedItem : requestedItems) {

            int bookId = requestedItem.getBookId();
            int quantity = requestedItem.getQuantity();

            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero."
                );
            }

            if (!processedBookIds.add(bookId)) {
                throw new IllegalArgumentException(
                        "Duplicate book IDs are not allowed in an order."
                );
            }

            Book book = bookService.getBookById(bookId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Book with ID " + bookId + " was not found."
                            )
                    );

            if (book.getStockQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for: " + book.getTitle()
                );
            }

            // Use trusted book details, not details supplied by the customer
            OrderItem validatedItem = new OrderItem(
                    book.getId(),
                    book.getTitle(),
                    quantity,
                    book.getPrice()
            );

            validatedItems.add(validatedItem);
        }

        // Generate the next order ID
        int nextOrderId = 1;

        try {
            List<Order> existingOrders =
                    orderFileManager.loadOrders();

            for (Order order : existingOrders) {
                if (order.getOrderId() >= nextOrderId) {
                    nextOrderId = order.getOrderId() + 1;
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load existing orders.", e
            );
        }

        // Create the order
        Order newOrder = new Order(
                nextOrderId,
                customerId,
                validatedItems
        );

        // Reduce stock and keep track of successful reductions
        List<OrderItem> reducedItems = new ArrayList<>();

        try {
            for (OrderItem item : validatedItems) {
                bookService.reduceStock(
                        item.getBookId(),
                        item.getQuantity()
                );

                reducedItems.add(item);
            }

            orderFileManager.saveOrder(newOrder);

            return newOrder;

        } catch (IOException | RuntimeException e) {

            // Restore stock if saving the order fails
            for (OrderItem item : reducedItems) {
                try {
                    bookService.restoreStock(
                            item.getBookId(),
                            item.getQuantity()
                    );
                } catch (RuntimeException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw new IllegalStateException(
                    "Could not complete the order.", e
            );
        }
    }

    // 2. Get all orders
    public List<Order> getAllOrders() {
        try {
            return orderFileManager.loadOrders();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not retrieve orders.", e
            );
        }
    }

    // 3. Find an order by ID
    public Optional<Order> getOrderById(int orderId) {
        try {
            return orderFileManager.findOrderById(orderId);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not retrieve the order.", e
            );
        }
    }

    // 4. Get all orders belonging to a customer
    public List<Order> getOrdersByCustomerId(String customerId) {
        try {
            return orderFileManager.findOrdersByCustomerId(customerId);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not retrieve customer orders.", e
            );
        }
    }


    // 5. Update the status of an existing order
    public Order updateOrderStatus(int orderId, OrderStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New order status cannot be null."
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Use cancelOrder() to cancel an order."
            );
        }

        Order order = getOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order with ID " + orderId + " was not found."
                        )
                );

        OrderStatus currentStatus = order.getStatus();

        boolean validTransition =
                (currentStatus == OrderStatus.PENDING
                        && newStatus == OrderStatus.CONFIRMED)
                        || (currentStatus == OrderStatus.CONFIRMED
                        && newStatus == OrderStatus.PROCESSING)
                        || (currentStatus == OrderStatus.PROCESSING
                        && newStatus == OrderStatus.SHIPPED)
                        || (currentStatus == OrderStatus.SHIPPED
                        && newStatus == OrderStatus.DELIVERED);

        if (!validTransition) {
            throw new IllegalStateException(
                    "Cannot change order status from "
                            + currentStatus + " to " + newStatus
            );
        }

        order.setStatus(newStatus);

        try {
            boolean updated = orderFileManager.updateOrder(order);

            if (!updated) {
                throw new IllegalStateException(
                        "Could not update the order."
                );
            }

            return order;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not save the updated order status.", e
            );
        }
    }


    // 6. Cancel an existing order
    public Order cancelOrder(int orderId) {

        Order order = getOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order with ID " + orderId + " was not found."
                        )
                );

        // Prevent repeated cancellation
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "This order has already been cancelled."
            );
        }

        // Prevent cancellation after processing has started
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "This order cannot be cancelled because it is already "
                            + order.getStatus()
            );
        }

        // Restore stock and track successful restorations
        List<OrderItem> restoredItems = new ArrayList<>();

        try {
            for (OrderItem item : order.getItems()) {

                bookService.restoreStock(
                        item.getBookId(),
                        item.getQuantity()
                );

                restoredItems.add(item);
            }

            // Change order status
            order.setStatus(OrderStatus.CANCELLED);

            // Mark paid orders as refunded
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }

            // Save the updated order
            boolean updated = orderFileManager.updateOrder(order);

            if (!updated) {
                throw new IllegalStateException(
                        "Could not update the cancelled order."
                );
            }

            return order;

        } catch (IOException | RuntimeException e) {

            // Roll back any stock that was already restored
            for (OrderItem item : restoredItems) {

                try {
                    bookService.reduceStock(
                            item.getBookId(),
                            item.getQuantity()
                    );

                } catch (RuntimeException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw new IllegalStateException(
                    "Could not complete order cancellation.", e
            );
        }
    }


}

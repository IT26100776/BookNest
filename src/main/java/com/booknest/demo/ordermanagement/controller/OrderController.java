
package com.booknest.demo.ordermanagement.controller;

import com.booknest.demo.ordermanagement.dto.OrderRequest;
import com.booknest.demo.ordermanagement.dto.OrderStatusRequest;
import com.booknest.demo.ordermanagement.model.Order;
import com.booknest.demo.ordermanagement.model.OrderItem;
import com.booknest.demo.ordermanagement.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. Create a new order
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody OrderRequest request) {

        if (request == null || request.getItems() == null
                || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<OrderItem> items = new ArrayList<>();

        for (var item : request.getItems()) {
            items.add(new OrderItem(
                    item.getBookId(),
                    "",
                    item.getQuantity(),
                    null
            ));
        }

        Order createdOrder = orderService.createOrder(
                request.getCustomerId(), items);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    // 2. Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 3. Get an order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable int id) {

        Optional<Order> order = orderService.getOrderById(id);

        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Get orders belonging to a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(
            @PathVariable String customerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByCustomerId(customerId));
    }

    // 5. Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable int id,
            @RequestBody OrderStatusRequest request) {

        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest().build();
        }

        orderService.updateOrderStatus(id, request.getStatus());

        return ResponseEntity.noContent().build();
    }

    // 6. Cancel an order
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable int id) {

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }
}


package com.booknest.demo.ordermanagement.dto;

import com.booknest.demo.ordermanagement.model.OrderStatus;

public class OrderStatusRequest {

    private OrderStatus status;

    public OrderStatusRequest() {
    }

    public OrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

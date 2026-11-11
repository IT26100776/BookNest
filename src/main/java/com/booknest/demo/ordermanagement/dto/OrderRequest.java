
package com.booknest.demo.ordermanagement.dto;

import java.util.List;

public class OrderRequest {

    private String customerId;
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public OrderRequest(String customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}

package com.booknest.demo.shoppingcart;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private int customerId;
    private List<CartItem> items;

    public Cart(int customerId){
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    public int getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems(){
        return items;
    }

}

package com.booknest.demo;

import com.booknest.demo.shoppingcart.Cart;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testAddItem() {

        Cart cart = new Cart(101);

        cart.addItem(1, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testAddSameBookTwice() {

        Cart cart = new Cart(101);

        cart.addItem(1, 2);
        cart.addItem(1, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testRemoveItem() {

        Cart cart = new Cart(101);

        cart.addItem(1, 2);
        cart.removeItem(1);

        assertEquals(0, cart.getItems().size());
    }

    @Test
    void testUpdateQuantity() {

        Cart cart = new Cart(101);

        cart.addItem(1, 2);
        cart.updateQuantity(1, 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testClearCart() {

        Cart cart = new Cart(101);

        cart.addItem(1, 2);
        cart.addItem(2, 3);

        cart.clearCart();

        assertEquals(0, cart.getItems().size());
    }
}



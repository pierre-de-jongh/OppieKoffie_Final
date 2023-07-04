package com.example.myapplication;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private List<CartItem> cartItems = new ArrayList<>();

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addToCart(CartItem cartItem) {
        cartItems.add(cartItem);
    }
}

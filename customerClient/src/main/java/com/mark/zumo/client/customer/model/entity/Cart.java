package com.mark.zumo.client.customer.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {
    private List<CartItem> cartItemList;

    public Cart() {
        cartItemList = new ArrayList<>();
    }

    public void addCartItem(CartItem cartItem) {
        cartItemList.add(cartItem);
    }

    public void removeCartItem(int position) {
        cartItemList.remove(position);
    }

    public int getCartCount() {
        return cartItemList.size();
    }
}

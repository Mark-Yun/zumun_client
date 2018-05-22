package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 18. 5. 22.
 */
public enum CartManager {
    INSTANCE;

    private Map<String, Cart> storeCartMap;

    CartManager() {
        storeCartMap = new HashMap<>();
    }

    public Cart getCart(String storeUuid) {
        if (!storeCartMap.containsKey(storeUuid)) {
            Cart cart = new Cart();
            storeCartMap.put(storeUuid, cart);
        }

        return storeCartMap.get(storeUuid);
    }
}

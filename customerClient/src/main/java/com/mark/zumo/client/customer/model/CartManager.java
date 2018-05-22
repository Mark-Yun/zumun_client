package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mark on 18. 5. 22.
 */
public enum CartManager {
    INSTANCE;

    private Map<UUID, Cart> storeCartMap;

    CartManager() {
        storeCartMap = new HashMap<>();
    }

    public Cart getCart(UUID storeUuid) {
        if (!storeCartMap.containsKey(storeUuid)) {
            Cart cart = new Cart();
            storeCartMap.put(storeUuid, cart);
        }

        return storeCartMap.get(storeUuid);
    }
}

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 22.
 */
public enum CartManager {
    INSTANCE;

    private Map<String, Cart> storeCartMap;

    CartManager() {
        storeCartMap = new HashMap<>();
    }

    public Observable<Cart> getCart(final String storeUuid) {
        if (!storeCartMap.containsKey(storeUuid)) {
            Cart cart = new Cart();
            storeCartMap.put(storeUuid, cart);
        }

        return Observable.create((ObservableOnSubscribe<Cart>) e -> {
            e.onNext(storeCartMap.get(storeUuid).addEmitter(e));
        }).subscribeOn(Schedulers.computation());
    }

    public void clearCart(final String storeUuid) {
        Cart cart = storeCartMap.get(storeUuid);
        if (cart == null) {
            return;
        }

        cart.clear();
    }
}

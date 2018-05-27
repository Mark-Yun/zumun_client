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
        return Observable.create((ObservableOnSubscribe<Cart>) e -> {
            if (!storeCartMap.containsKey(storeUuid)) {
                Cart cart = new Cart(e);
                storeCartMap.put(storeUuid, cart);
            }
            e.onNext(storeCartMap.get(storeUuid));
        }).subscribeOn(Schedulers.computation());
    }
}

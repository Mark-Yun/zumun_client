package com.mark.zumo.server.store.model;

import com.mark.zumo.client.core.entity.MenuOrder;

import io.reactivex.Observable;


/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuOrderManager {
    INSTANCE;

    public Observable<MenuOrder> getMenuOrder() {
        return Observable.create(e -> {
            for (int i = 0; i < 10; i++) {
                e.onNext(new MenuOrder(i, 0, 0, null, 0, 5500));
                Thread.sleep(2000);
            }
        });
    }
}

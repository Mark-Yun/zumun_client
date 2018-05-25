package com.mark.zumo.server.store.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.DebugUtil;

import io.reactivex.Observable;


/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuOrderManager {
    INSTANCE;

    public Observable<MenuOrder> getMenuOrder() {
        return Observable.create(e -> {
            for (int i = 0; i < 10; i++) {
                e.onNext(DebugUtil.menuOrder());
                Thread.sleep(2000);
            }
        });
    }
}

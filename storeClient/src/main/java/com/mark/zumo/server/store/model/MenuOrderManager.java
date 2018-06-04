/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.DebugUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuOrderManager {
    INSTANCE;

    MenuOrderManager() {
    }

    public Observable<MenuOrder> getMenuOrder() {
        return Observable.create((ObservableOnSubscribe<MenuOrder>) e -> {
            for (int i = 0; i < 10; i++) {
                e.onNext(DebugUtil.menuOrder());
                Thread.sleep(2000);
            }
        }).subscribeOn(Schedulers.computation());
    }
}

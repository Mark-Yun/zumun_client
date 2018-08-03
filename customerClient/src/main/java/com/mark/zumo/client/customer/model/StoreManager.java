/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.location.Location;
import android.util.Log;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreManager {
    INSTANCE;

    private static final String TAG = "StoreManager";

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    StoreManager() {
        storeRepository = StoreRepository.INSTANCE;
        orderRepository = OrderRepository.INSTANCE;
    }

    public Maybe<List<Store>> nearByStore(Location location) {
        return storeRepository.nearByStore(location)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Store>> latestVisitStore(String customerUuid, int limit) {
        return orderRepository.getMenuOrderListByCustomerUuid(customerUuid, 0, 10)
                .flatMap(Observable::fromIterable)
                .map(menuOrder -> menuOrder.storeUuid)
                .distinct().take(limit)
                .flatMapMaybe(storeRepository::getStoreFromApi)
                .toList()
                .toMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepository.getStore(storeUuid)
                .subscribeOn(Schedulers.io())
                .doOnNext(store -> Log.d(TAG, "getStore: " + store));
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return storeRepository.getStoreFromDisk(storeUuid)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(store -> Log.d(TAG, "getStoreFromDisk: " + store));
    }
}

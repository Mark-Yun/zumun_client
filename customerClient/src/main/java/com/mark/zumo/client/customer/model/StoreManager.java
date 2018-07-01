/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.util.Log;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.provider.AppLocationProvider;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreManager {
    INSTANCE;

    private static final String TAG = "StoreManager";

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    private final AppLocationProvider locationProvider;

    StoreManager() {
        storeRepository = StoreRepository.INSTANCE;
        locationProvider = AppLocationProvider.INSTANCE;
        orderRepository = OrderRepository.INSTANCE;
    }

    public Observable<List<Store>> nearByStore() {
        return locationProvider.currentLocationObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(storeRepository::nearByStore)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(stores -> Log.d(TAG, "nearByStore: " + stores.size()));
    }

    public Maybe<List<Store>> latestVisitStore(String customerUuid, int limit) {
        return orderRepository.getMenuOrderListByCustomerUuidFromDisk(customerUuid, 0, 10)
                .flatMapObservable(Observable::fromIterable)
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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.util.Log;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.provider.AppLocationProvider;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreManager {
    INSTANCE;

    private static final String TAG = "StoreManager";
    private StoreRepository storeRepository;
    private AppLocationProvider locationProvider;

    StoreManager() {
        storeRepository = StoreRepository.INSTANCE;
        locationProvider = AppLocationProvider.INSTANCE;
    }

    public Observable<List<Store>> nearByStore() {
        return locationProvider.currentLocation()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(storeRepository::nearByStore)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(stores -> Log.d(TAG, "nearByStore: " + stores.size()));
    }

    public Observable<List<Store>> latestVisitStore() {
        return storeRepository.latestVisitStore()
                .subscribeOn(Schedulers.io())
                .doOnNext(stores -> Log.d(TAG, "latestVisitStore: " + stores.size()));
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepository.getStore(storeUuid)
                .subscribeOn(Schedulers.io())
                .doOnNext(store -> Log.d(TAG, "getStore: " + store));
    }

    public Observable<Store> getStoreFromDisk(String storeUuid) {
        return storeRepository.getStoreFromDisk(storeUuid)
                .subscribeOn(Schedulers.io())
                .doOnNext(store -> Log.d(TAG, "getStoreFromDisk: " + store));
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum CustomerStoreManager {
    INSTANCE;

    private static final String TAG = "CustomerStoreManager";

    private final StoreRepository storeRepository;

    CustomerStoreManager() {
        storeRepository = StoreRepository.INSTANCE;
    }

    public Maybe<List<Store>> nearByStore(LatLng latLng, double distanceKm) {
        return storeRepository.nearByStore(latLng.latitude, latLng.longitude, distanceKm)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepository.getStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return storeRepository.getStoreFromDisk(storeUuid)
                .subscribeOn(Schedulers.io());
    }
}

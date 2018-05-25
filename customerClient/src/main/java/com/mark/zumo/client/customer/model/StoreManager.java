package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.provider.AppLocationProvider;
import com.mark.zumo.client.core.repository.StoreRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreManager {
    INSTANCE;

    private StoreRepository storeRepository;
    private AppLocationProvider locationProvider;

    StoreManager() {
        storeRepository = StoreRepository.from(ContextHolder.getContext());
        locationProvider = AppLocationProvider.INSTANCE;
    }

    public Observable<List<Store>> nearByStore() {
        return locationProvider.currentLocation()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(storeRepository::nearByStore);
    }

    public Observable<List<Store>> latestVisitStore() {
        return storeRepository.latestVisitStore();
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepository.getStore(storeUuid);
    }
}

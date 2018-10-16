/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.BundleUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public class StoreRepository {

    private static final String TAG = "StoreRepository";

    private static StoreRepository sInstance;
    private static Bundle session;

    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private StoreRepository(final Bundle session) {
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        StoreRepository.session = session;
    }

    public static StoreRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(StoreRepository.session, session)) {
            synchronized (StoreRepository.class) {
                if (sInstance == null) {
                    sInstance = new StoreRepository(session);
                }
            }
        }

        return sInstance;
    }

    public Maybe<List<Store>> nearByStore(double latitude, double longitude, double distanceKm) {
        return networkRepository.getNearByStore(latitude, longitude, distanceKm);
    }

    public Maybe<Store> updateStore(Store store) {
        return networkRepository.updateStore(store.uuid, store)
                .doOnSuccess(diskRepository::insertStore);
    }

    public Observable<Store> getStore(String storeUuid) {
        Maybe<Store> storeDB = diskRepository.getStore(storeUuid);
        Maybe<Store> storeApi = networkRepository.getStore(storeUuid)
                .doOnSuccess(diskRepository::insertStore);

        return Maybe.merge(storeDB, storeApi)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged();
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return diskRepository.getStore(storeUuid);
    }

    public Maybe<Store> getStoreFromApi(String storeUuid) {
        return networkRepository.getStore(storeUuid)
                .doOnSuccess(diskRepository::insertStore);
    }
}

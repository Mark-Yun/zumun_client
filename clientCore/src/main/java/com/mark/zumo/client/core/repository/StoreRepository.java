/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.location.Location;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreRepository {
    INSTANCE;

    private static final String TAG = "StoreRepository";

    private final DiskRepository diskRepository;

    StoreRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository;
    }

    public Maybe<List<Store>> nearByStore(Location location) {
        return networkRepository().getNearByStore(location.getLongitude(), location.getLatitude(), 3);
    }

    public Observable<List<Store>> latestVisitStore() {
        return Observable.create(e -> e.onNext(DebugUtil.storeList()));
    }

    public Maybe<Store> updateStore(Store store) {
        return networkRepository().updateStore(store.uuid, store)
                .doOnSuccess(diskRepository::insertStore);
    }

    public Observable<Store> getStore(String storeUuid) {
        Maybe<Store> storeDB = diskRepository.getStore(storeUuid);
        Maybe<Store> storeApi = networkRepository().getStore(storeUuid)
                .doOnSuccess(diskRepository::insertStore);

        return Maybe.merge(storeDB, storeApi)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new EntityComparator<>());
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return diskRepository.getStore(storeUuid);
    }

    public Maybe<Store> getStoreFromApi(String storeUuid) {
        return networkRepository().getStore(storeUuid)
                .doOnSuccess(diskRepository::insertStore);
    }
}

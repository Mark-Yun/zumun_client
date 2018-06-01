/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.location.Location;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreRepository {
    INSTANCE;

    public static final String TAG = "StoreRepository";

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    StoreRepository() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Observable<List<Store>> nearByStore(Location location) {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }

    public Observable<List<Store>> latestVisitStore() {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }

    public Observable<Store> getStore(String storeUuid) {
        Observable<Store> storeDB = diskRepository.getStore(storeUuid).toObservable();
        Observable<Store> storeApi = networkRepository.getStore(storeUuid)
                .doOnNext(diskRepository::insert);

        return Observable.merge(storeDB, storeApi)
                .doOnError(this::onErrorOccurred)
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new EntityComparator<>());
    }

    public Observable<Store> getStoreFromDisk(String storeUuid) {
        return diskRepository.getStore(storeUuid).toObservable()
                .doOnError(this::onErrorOccurred);
    }


    private void onErrorOccurred(Throwable throwable) {
        Log.e(TAG, "onErrorOccurred: ", throwable);
    }
}

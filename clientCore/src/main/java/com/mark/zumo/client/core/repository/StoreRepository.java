/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.appserver.response.store.registration.StoreRegistrationResponse;
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
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return diskRepository.getStore(storeUuid);
    }

    public Maybe<Store> getStoreFromApi(String storeUuid) {
        return networkRepository.getStore(storeUuid)
                .doOnSuccess(diskRepository::insertStore);
    }

    public Observable<List<StoreRegistrationRequest>> getStoreRegistrationRequestListByStoreUserUuid(String storeUserUuid) {
        Maybe<List<StoreRegistrationRequest>> storeRegistrationRequestListApi = networkRepository.getStoreRegistrationRequestByStoreUserUuid(storeUserUuid);
        Maybe<List<StoreRegistrationRequest>> storeRegistrationRequestListByDB = diskRepository.getStoreRegistrationRequestListByStoreUserUuid(storeUserUuid);

        return Maybe.merge(storeRegistrationRequestListByDB, storeRegistrationRequestListApi)
                .toObservable();
    }

    public Observable<List<StoreRegistrationRequest>> getStoreRegistrationRequestListAll(int limit) {
        Maybe<List<StoreRegistrationRequest>> storeRegistrationRequestListApi = networkRepository.getStoreRegistrationRequestAll(limit)
                .doOnSuccess(diskRepository::insertStoreRegistrationRequestList);
        Maybe<List<StoreRegistrationRequest>> storeRegistrationRequestListByDB = diskRepository.getStoreRegistrationRequestAll(limit);

        return Maybe.merge(storeRegistrationRequestListByDB, storeRegistrationRequestListApi)
                .toObservable();
    }

    public Maybe<StoreRegistrationRequest> getStoreRegistrationRequestByUuidFromDisk(String uuid) {
        return diskRepository.getStoreRegistrationRequestByUuid(uuid);
    }

    public Maybe<StoreRegistrationResult> approveStoreRegistration(StoreRegistrationRequest storeRegistrationRequest) {
        return networkRepository.approveStoreRegistration(storeRegistrationRequest)
                .doOnSuccess(diskRepository::insertStoreRegistrationResult);
    }

    public Maybe<StoreRegistrationResult> rejectStoreRegistration(StoreRegistrationRequest storeRegistrationRequest) {
        return networkRepository.rejectStoreRegistration(storeRegistrationRequest)
                .doOnSuccess(diskRepository::insertStoreRegistrationResult);
    }

    public Maybe<StoreRegistrationResponse> createStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        return networkRepository.createStoreRegistrationRequest(storeRegistrationRequest);
    }

    public Observable<List<StoreRegistrationResult>> getStoreRegistrationResultListByStoreUserUuid(String storeUserUuid) {
        Maybe<List<StoreRegistrationResult>> storeRegistrationResultListApi = networkRepository.getStoreRegistrationResultByStoreUserUuid(storeUserUuid);
        Maybe<List<StoreRegistrationResult>> storeRegistrationResultListByDB = diskRepository.getStoreRegistrationResultByStoreUserUuid(storeUserUuid);

        return Maybe.merge(storeRegistrationResultListByDB, storeRegistrationResultListApi)
                .toObservable();
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreStoreManager {

    INSTANCE;

    private final SessionRepository sessionRepository;
    private final Maybe<StoreRepository> storeRepositoryMaybe;

    StoreStoreManager() {
        sessionRepository = SessionRepository.INSTANCE;
        storeRepositoryMaybe = sessionRepository.getStoreSession()
                .map(StoreRepository::getInstance);
    }

    public Maybe<Store> updateStoreName(Store store, String newName) {
        store.name = newName;
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(store))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreLocation(Store store, LatLng latLng) {
        store.latitude = latLng.latitude;
        store.longitude = latLng.longitude;
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(store))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreCoverImageUrl(Store store, String coverImageUrl) {
        store.coverImageUrl = coverImageUrl;
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(store))
                .subscribeOn(Schedulers.io());

    }

    public Maybe<Store> updateStoreThumbnailImageUrl(Store store, String thumbnailImageUrl) {
        store.thumbnailUrl = thumbnailImageUrl;
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(store))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreRegistrationRequest>> getStoreRegistrationByStoreUserUuid(String storeUserUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStoreRegistrationRequestListByStoreUserUuid(storeUserUuid)
        );
    }

    public Observable<List<StoreRegistrationResult>> getStoreRegistrationResultByRequestId(long requestId) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStoreRegistrationResultListByRequestId(requestId)
        );
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStore(storeUuid)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationRequest> createStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepositoryMaybe.flatMap(storeRepository ->
                storeRepository.createStoreRegistrationRequest(storeRegistrationRequest)
        );
    }
}

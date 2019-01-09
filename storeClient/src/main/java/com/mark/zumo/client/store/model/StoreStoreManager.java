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
        Store newStore = Store.Builder.from(store)
                .setName(newName)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreLocation(Store store, LatLng latLng) {
        Store newStore = Store.Builder.from(store)
                .setLatitude(latLng.latitude)
                .setLongitude(latLng.longitude)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreCoverImageUrl(Store store, String coverImageUrl) {
        Store newStore = Store.Builder.from(store)
                .setCoverImageUrl(coverImageUrl)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());

    }

    public Maybe<Store> updateStoreThumbnailImageUrl(Store store, String thumbnailImageUrl) {
        Store newStore = Store.Builder.from(store)
                .setThumbnailUrl(thumbnailImageUrl)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreRegistrationRequest>> getStoreRegistrationRequestByStoreUserUuid(String storeUserUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStoreRegistrationRequestListByStoreUserUuid(storeUserUuid)
        ).distinctUntilChanged().subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreRegistrationResult>> getStoreRegistrationResultByRequestId(String storeRegistrationRequestUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStoreRegistrationResultListByRequestId(storeRegistrationRequestUuid)
        ).distinctUntilChanged().subscribeOn(Schedulers.io());
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStore(storeUuid)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationRequest> createStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepositoryMaybe.flatMap(storeRepository ->
                storeRepository.createStoreRegistrationRequest(storeRegistrationRequest)
        ).subscribeOn(Schedulers.io());
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
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

public enum StoreManager {
    INSTANCE;

    private static final String TAG = "StoreManager";

    private final SessionRepository sessionRepository;
    private final Maybe<StoreRepository> storeRepositoryMaybe;

    StoreManager() {
        sessionRepository = SessionRepository.INSTANCE;
        storeRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(StoreRepository::getInstance);
    }

    public Maybe<List<Store>> nearByStore(Location location) {
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.nearByStore(location, 1))
                .flatMapObservable(Observable::fromIterable)
                .sorted((s1, s2) -> (int) (location.distanceTo(from(s1.latitude, s1.longitude)) - location.distanceTo(from(s2.latitude, s2.longitude))))
                .toList().toMaybe()
                .subscribeOn(Schedulers.io());
    }

    private Location from(double latitude, double longitude) {
        Location location = new Location("Place Point");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public Maybe<List<Store>> nearByStore(LatLng latLng, int distanceMeter) {
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.nearByStore(latLng.latitude, latLng.longitude, distanceMeter))
                .subscribeOn(Schedulers.io());
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository -> storeRepository.getStore(storeUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> getStoreFromDisk(String storeUuid) {
        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.getStoreFromDisk(storeUuid))
                .subscribeOn(Schedulers.io());
    }
}

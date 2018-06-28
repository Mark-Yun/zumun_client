/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.location.Location;

import com.mark.zumo.client.core.provider.AppLocationProvider;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 24.
 */
public enum CustomerLocationManager {
    INSTANCE;

    private final AppLocationProvider locationProvider;

    CustomerLocationManager() {
        locationProvider = AppLocationProvider.INSTANCE;
    }

    public Observable<Float> distanceFrom(double latitude, double longitude) {
        Location location = from(latitude, longitude);
        return locationProvider.currentLocationObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(location1 -> location1.distanceTo(location))
                .subscribeOn(Schedulers.io());
    }

    private Location from(double latitude, double longitude) {
        Location location = new Location("Place Point");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.location.Location;

import com.mark.zumo.client.core.device.AppLocationProvider;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
                .map(location1 -> location1.distanceTo(location))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    private Location from(double latitude, double longitude) {
        Location location = new Location("Place Point");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}

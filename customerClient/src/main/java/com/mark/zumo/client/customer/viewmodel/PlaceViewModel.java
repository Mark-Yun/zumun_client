/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.device.AppLocationProvider;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.CustomerLocationManager;
import com.mark.zumo.client.customer.model.CustomerStoreManager;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 19.
 */
public class PlaceViewModel extends AndroidViewModel {

    private final CustomerStoreManager customerStoreManager;
    private final CustomerLocationManager locationManager;
    private final AppLocationProvider locationProvider;

    private final CompositeDisposable disposables;

    public PlaceViewModel(@NonNull final Application application) {
        super(application);

        customerStoreManager = CustomerStoreManager.INSTANCE;
        locationManager = CustomerLocationManager.INSTANCE;
        locationProvider = AppLocationProvider.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<List<Store>> nearByStore(LatLng latLng, double distanceKm) {
        MutableLiveData<List<Store>> nearByStore = new MutableLiveData<>();

        customerStoreManager.nearByStore(latLng, distanceKm)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(nearByStore::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return nearByStore;
    }

    public LiveData<LatLng> currentLocation() {
        MutableLiveData<LatLng> liveData = new MutableLiveData<>();

        locationProvider.currentLocationObservable
                .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<String> distanceFrom(double latitude, double longitude) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        locationManager.distanceFrom(latitude, longitude)
                .map(this::convertDistance)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    private String convertDistance(float distance) {
        if (distance < 100) {
            return getApplication().getString(R.string.distance_description_under_100m);
        } else {
            String distKm = String.format(Locale.getDefault(), "%.1f", distance / 1000);
            return getApplication().getString(R.string.distance_format_kilo_meter, distKm);
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}

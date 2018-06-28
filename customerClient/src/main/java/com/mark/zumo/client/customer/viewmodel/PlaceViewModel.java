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

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.CustomerLocationManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 19.
 */
public class PlaceViewModel extends AndroidViewModel {

    private final StoreManager storeManager;
    private final CustomerLocationManager locationManager;

    private final CompositeDisposable disposables;

    public PlaceViewModel(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;
        disposables = new CompositeDisposable();
        locationManager = CustomerLocationManager.INSTANCE;
    }

    public LiveData<List<Store>> nearByStore() {
        MutableLiveData<List<Store>> nearByStore = new MutableLiveData<>();

        storeManager.nearByStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(nearByStore::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return nearByStore;
    }

    public LiveData<List<Store>> latestVisitStore() {
        MutableLiveData<List<Store>> latestVisitStore = new MutableLiveData<>();

        storeManager.latestVisitStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(latestVisitStore::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return latestVisitStore;
    }

    public LiveData<String> distanceFrom(double latitude, double longitude) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        locationManager.distanceFrom(latitude, longitude)
                .map(this::convertKm)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    private String convertKm(float distance) {
        if (distance < 1000) {
            return getApplication().getString(R.string.distance_format_meter, String.valueOf(distance));
        } else {
            String distKm = String.format("%.2f", distance / 1000);
            return getApplication().getString(R.string.distance_format_kilo_meter, distKm);
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}

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
import com.mark.zumo.client.customer.model.SessionManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 19.
 */
public class PlaceViewModel extends AndroidViewModel {

    private final StoreManager storeManager;
    private final CustomerLocationManager locationManager;
    private final SessionManager sessionManager;

    private final CompositeDisposable disposables;

    public PlaceViewModel(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;
        sessionManager = SessionManager.INSTANCE;
        locationManager = CustomerLocationManager.INSTANCE;

        disposables = new CompositeDisposable();
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

        sessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMap(userUuid -> storeManager.latestVisitStore(userUuid, 5))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(latestVisitStore::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return latestVisitStore;
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
        if (distance < 1000) {
            String distMeter = String.format(Locale.getDefault(), "%.2f", distance);
            return getApplication().getString(R.string.distance_format_meter, distMeter);
        } else {
            String distKm = String.format(Locale.getDefault(), "%.2f", distance / 1000);
            return getApplication().getString(R.string.distance_format_kilo_meter, distKm);
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}

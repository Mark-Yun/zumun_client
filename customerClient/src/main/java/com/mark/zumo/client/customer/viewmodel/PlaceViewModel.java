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
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 19.
 */
public class PlaceViewModel extends AndroidViewModel {

    private StoreManager storeManager;

    private CompositeDisposable disposables;

    public PlaceViewModel(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;
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

        storeManager.latestVisitStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(latestVisitStore::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return latestVisitStore;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.supervisor.model.StoreRegistrationManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 12. 30.
 */
public class StoreRegistrationViewModel extends AndroidViewModel {

    private final StoreRegistrationManager storeRegistrationManager;

    private final CompositeDisposable compositeDisposable;

    public StoreRegistrationViewModel(@NonNull final Application application) {
        super(application);

        storeRegistrationManager = StoreRegistrationManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<StoreRegistrationRequest>> getStoreRegistrationRequest() {
        MutableLiveData<List<StoreRegistrationRequest>> liveData = new MutableLiveData<>();

        storeRegistrationManager.getStoreRegistrationRequestList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<StoreRegistrationRequest> getStoreRegistrationRequestByUuidFromDisk(String requestUuid) {
        MutableLiveData<StoreRegistrationRequest> liveData = new MutableLiveData<>();

        storeRegistrationManager.getStoreRegistrationRequestByUuidFromDisk(requestUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<StoreRegistrationResult> approve(String requestUuid) {
        MutableLiveData<StoreRegistrationResult> liveData = new MutableLiveData<>();
        storeRegistrationManager.getStoreRegistrationRequestByUuidFromDisk(requestUuid)
                .flatMap(storeRegistrationManager::approveStoreRegistration)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<StoreRegistrationResult> reject(String requestUuid, String comment) {
        MutableLiveData<StoreRegistrationResult> liveData = new MutableLiveData<>();

        storeRegistrationManager.getStoreRegistrationRequestByUuidFromDisk(requestUuid)
                .flatMap(storeRegistrationManager::rejectStoreRegistration)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }
}

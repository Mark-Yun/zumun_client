/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.response.store.registration.StoreRegistrationResponse;
import com.mark.zumo.client.core.device.AppLocationProvider;
import com.mark.zumo.client.store.model.StoreS3TransferManager;
import com.mark.zumo.client.store.model.StoreStoreManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 12. 26.
 */
public class StoreRegistrationViewModel extends AndroidViewModel {

    private static final String TAG = "StoreRegistrationViewModel";

    private final StoreUserManager storeUserManager;
    private final StoreStoreManager storeStoreManager;
    private final AppLocationProvider appLocationProvider;
    private final StoreS3TransferManager storeS3TransferManager;

    private final CompositeDisposable compositeDisposable;

    public StoreRegistrationViewModel(@NonNull final Application application) {
        super(application);

        storeUserManager = StoreUserManager.INSTANCE;
        storeStoreManager = StoreStoreManager.INSTANCE;
        appLocationProvider = AppLocationProvider.INSTANCE;
        storeS3TransferManager = StoreS3TransferManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<StoreRegistrationRequest>> getCombinedStoreRegistrationRequest() {
        MutableLiveData<List<StoreRegistrationRequest>> liveData = new MutableLiveData<>();

        storeUserManager.getStoreUserSession()
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMapObservable(storeStoreManager::getCombinedStoreRegistrationRequestByStoreUserUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<StoreRegistrationResponse> createStoreRegistrationRequest(final Activity activity,
                                                                              final StoreRegistrationRequest storeRegistrationRequest) {
        MutableLiveData<StoreRegistrationResponse> liveData = new MutableLiveData<>();

        storeUserManager.getStoreUserSession()
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMap(storeUserUuid ->
                        storeS3TransferManager.uploadCorporateScanImage(activity, storeUserUuid, Uri.parse(storeRegistrationRequest.corporateRegistrationScanUrl))
                                .map(uploadedUrl ->
                                        new StoreRegistrationRequest.Builder(storeRegistrationRequest)
                                                .setCorporateRegistrationScanUrl(uploadedUrl)
                                                .setStoreUserUuid(storeUserUuid)
                                                .buildIgnoreException())
                )
                .flatMap(storeStoreManager::createStoreRegistrationRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<Location> getCurrentLocation() {
        MutableLiveData<Location> liveData = new MutableLiveData<>();

        appLocationProvider.currentLocationObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}

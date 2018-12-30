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
import android.util.Log;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.provider.AppLocationProvider;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.StoreStoreManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import java.util.List;

import io.reactivex.Maybe;
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
    private final S3TransferManager s3TransferManager;

    private final CompositeDisposable compositeDisposable;

    public StoreRegistrationViewModel(@NonNull final Application application) {
        super(application);

        storeUserManager = StoreUserManager.INSTANCE;
        storeStoreManager = StoreStoreManager.INSTANCE;
        appLocationProvider = AppLocationProvider.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<StoreRegistrationRequest>> getCombinedStoreRegistrationRequest() {
        MutableLiveData<List<StoreRegistrationRequest>> liveData = new MutableLiveData<>();

        Maybe.fromCallable(storeUserManager::getStoreUserSessionSync)
                .switchIfEmpty(storeUserManager.getStoreUserSessionAsync())
                .doOnSuccess(a -> Log.d(TAG, "getCombinedStoreRegistrationRequest: " + a))
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMapObservable(storeStoreManager::getStoreRegistrationRequestByStoreUserUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnError(e -> Log.e(TAG, "getCombinedStoreRegistrationRequest: ", e))
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<StoreRegistrationResult>> getStoreRegistrationResult(String storeRegistrationRequestUuid) {
        MutableLiveData<List<StoreRegistrationResult>> liveData = new MutableLiveData<>();

        storeStoreManager.getStoreRegistrationResultByRequestId(storeRegistrationRequestUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<StoreRegistrationRequest> createStoreRegistrationRequest(final Activity activity,
                                                                             final StoreRegistrationRequest storeRegistrationRequest) {
        MutableLiveData<StoreRegistrationRequest> liveData = new MutableLiveData<>();

        Maybe.fromCallable(storeUserManager::getStoreUserSessionSync)
                .switchIfEmpty(storeUserManager.getStoreUserSessionAsync())
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMap(storeUserUuid ->
                        s3TransferManager.uploadCorporateScanImage(activity, storeUserUuid, Uri.parse(storeRegistrationRequest.corporateRegistrationScanUrl))
                                .map(uploadedUrl ->
                                        new StoreRegistrationRequest.Builder(storeRegistrationRequest)
                                                .setCorporateRegistrationScanUrl(uploadedUrl)
                                                .setStoreUserUuid(storeUserUuid)
                                                .build())
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

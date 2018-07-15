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
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.SessionManager;
import com.mark.zumo.client.store.model.StoreManager;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 25.
 */
public class StoreSettingViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;
    private final StoreManager storeManager;
    private final S3TransferManager s3TransferManager;

    private final CompositeDisposable compositeDisposable;

    public StoreSettingViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> getCurrentStore() {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Store> updateStoreName(String newName) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .flatMap(store -> storeManager.updateStoreName(store, newName))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Store> updateStoreLocation(LatLng latLng) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .flatMap(store -> storeManager.updateStoreLocation(store, latLng))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Store> uploadCoverImage(Activity activity, Uri uri) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        Maybe.zip(
                sessionManager.getSessionStore(),
                sessionManager.getSessionStore()
                        .map(store -> store.uuid)
                        .map(s3TransferManager::getCoverImageDirPath)
                        .flatMap(s3Path -> s3TransferManager.uploadFile(activity, s3Path, uri)),
                storeManager::updateStoreCoverImageUrl
        ).doOnSuccess(
                storeMaybe -> storeMaybe.observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(liveData::setValue)
                        .doOnSubscribe(compositeDisposable::add)
                        .subscribe()
        ).subscribe();

        return liveData;
    }

    public LiveData<Store> uploadThumbnailImage(Activity activity, Uri uri) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        Maybe.zip(
                sessionManager.getSessionStore(),
                sessionManager.getSessionStore()
                        .map(store -> store.uuid)
                        .map(s3TransferManager::getThumbnailImageDirPath)
                        .flatMap(s3Path -> s3TransferManager.uploadFile(activity, s3Path, uri)),
                storeManager::updateStoreThumbnailImageUrl
        ).doOnSuccess(
                storeMaybe -> storeMaybe.observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(liveData::setValue)
                        .doOnSubscribe(compositeDisposable::add)
                        .subscribe()
        ).subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.store.model.StoreS3TransferManager;
import com.mark.zumo.client.store.model.StoreStoreManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 25.
 */
public class StoreSettingViewModel extends AndroidViewModel {

    private final StoreUserManager storeUserManager;
    private final StoreStoreManager storeStoreManager;
    private final StoreS3TransferManager storeS3TransferManager;

    private final CompositeDisposable compositeDisposable;

    private MutableLiveData<Store> currentStore;

    public StoreSettingViewModel(@NonNull final Application application) {
        super(application);

        storeUserManager = StoreUserManager.INSTANCE;
        storeStoreManager = StoreStoreManager.INSTANCE;
        storeS3TransferManager = StoreS3TransferManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> getCurrentStore() {
        if (currentStore == null) {
            currentStore = new MutableLiveData<>();
        }

        loadOnCurrentStore();

        return currentStore;
    }

    public void loadOnCurrentStore() {
        storeUserManager.getSessionStoreAsync()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(currentStore::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
    }

    public LiveData<Store> updateStoreName(String newName) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        storeUserManager.getSessionStoreAsync()
                .flatMap(store -> storeUserManager.updateSessionStoreName(newName))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Store> updateStoreLocation(LatLng latLng) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        storeUserManager.getSessionStoreAsync()
                .flatMap(store -> storeUserManager.updateSessionStoreLocation(latLng))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Store> uploadCoverImage(Context context, Uri uri) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        storeUserManager.getSessionStoreAsync()
                .flatMap(store -> storeS3TransferManager.uploadCoverImage(context, store.uuid, uri))
                .flatMap(storeUserManager::updateSessionStoreCoverImageUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<Store> uploadThumbnailImage(Context context, Uri uri) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        storeUserManager.getSessionStoreAsync()
                .flatMap(store -> storeS3TransferManager.uploadThumbnailImage(context, store.uuid, uri))
                .flatMap(storeUserManager::updateSessionStoreThumbnailImageUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}

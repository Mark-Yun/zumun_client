/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.store.model.StoreStoreManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 19. 1. 13.
 */
public class WithdrawViewModel extends AndroidViewModel {

    private final StoreStoreManager storeStoreManager;
    private final StoreUserManager storeUserManager;

    private final CompositeDisposable compositeDisposable;

    public WithdrawViewModel(@NonNull final Application application) {
        super(application);
        storeStoreManager = StoreStoreManager.INSTANCE;
        storeUserManager = StoreUserManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Integer> getAvailableWithdrawMoney() {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();

        return liveData;
    }

    public LiveData<Object> withdraw(String string) {

        MutableLiveData<Object> liveData = new MutableLiveData<>();

        return liveData;
    }

    public LiveData<StoreOwner> getSessionStoreOwner() {

        MutableLiveData<StoreOwner> liveData = new MutableLiveData<>();
        Maybe.fromCallable(storeUserManager::getStoreUserSessionSync)
                .switchIfEmpty(storeUserManager.getStoreUserSessionAsync())
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMapObservable(storeUserManager::getStoreOwner)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

}

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
import android.support.annotation.NonNull;
import android.util.Log;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.P2pServer;
import com.mark.zumo.client.store.model.StoreSessionManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by mark on 18. 6. 9.
 */
public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private final StoreSessionManager storeSessionManager;
    private final StoreUserManager storeUserManager;
    private final CompositeDisposable compositeDisposable;
    private P2pServer p2pServer;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        storeSessionManager = StoreSessionManager.INSTANCE;
        storeUserManager = StoreUserManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> loadSessionStore() {
        MutableLiveData<Store> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public void findCustomer(Activity activity) {
        storeSessionManager.getSessionStore()
                .map(store -> p2pServer = new P2pServer(activity, store))
                .flatMapObservable(P2pServer::findCustomer)
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(customerUuid -> Log.d(TAG, "findCustomer: " + customerUuid))
                .subscribe();
    }

    public LiveData<Object> signOut() {
        MutableLiveData<Object> liveData = new MutableLiveData<>();
        storeUserManager.signOut()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> liveData.setValue(new Object()))
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        if (p2pServer != null) {
            p2pServer.stopAdvertising();
        }
    }
}

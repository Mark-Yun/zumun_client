/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.customer.model.SessionManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MainViewModel extends AndroidViewModel {

    private P2pClient p2pClient;
    private SessionManager sessionManager;
    private StoreManager storeManager;
    private CompositeDisposable compositeDisposable;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        p2pClient = P2pClient.INSTANCE;
        sessionManager = SessionManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> findStore(Activity activity) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        Maybe.fromCallable(sessionManager::getCurrentGuestUser)
                .map(guestUser -> guestUser.uuid)
                .flatMap(sessionId -> p2pClient.findStore(activity, sessionId))
                .flatMapObservable(storeId -> storeManager.getStore(storeId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<Menu>> requestMenuItemList(String storeUuid) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();

        Maybe.just(storeUuid)
                .flatMap(p2pClient::requestMenuItems)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        p2pClient.clear();
    }
}

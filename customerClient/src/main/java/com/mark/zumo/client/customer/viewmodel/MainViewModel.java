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

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.CustomerSessionManager;
import com.mark.zumo.client.customer.model.CustomerStoreManager;
import com.mark.zumo.client.customer.model.NotificationHandler;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MainViewModel extends AndroidViewModel {

    private final P2pClient p2pClient;
    private final CustomerSessionManager customerSessionManager;
    private final CustomerStoreManager customerStoreManager;
    private final CustomerOrderManager customerOrderManager;
    private final NotificationHandler notificationHandler;

    private final CompositeDisposable compositeDisposable;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        p2pClient = P2pClient.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;
        customerStoreManager = CustomerStoreManager.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
        notificationHandler = NotificationHandler.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> findStore(Activity activity) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        customerSessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMap(customerUuid -> p2pClient.findStore(activity, customerUuid))
                .flatMapObservable(customerStoreManager::getStore)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
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

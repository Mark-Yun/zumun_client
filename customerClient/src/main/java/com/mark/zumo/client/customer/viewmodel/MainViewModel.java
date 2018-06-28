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
import com.mark.zumo.client.customer.model.NotificationHandler;
import com.mark.zumo.client.customer.model.OrderManager;
import com.mark.zumo.client.customer.model.SessionManager;
import com.mark.zumo.client.customer.model.StoreManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MainViewModel extends AndroidViewModel {

    private final P2pClient p2pClient;
    private final SessionManager sessionManager;
    private final StoreManager storeManager;
    private final OrderManager orderManager;
    private final NotificationHandler notificationHandler;

    private final CompositeDisposable compositeDisposable;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        p2pClient = P2pClient.INSTANCE;
        sessionManager = SessionManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        orderManager = OrderManager.INSTANCE;
        notificationHandler = NotificationHandler.INSTANCE;

        compositeDisposable = new CompositeDisposable();
//        notificationHandler.requestOrderProgressNotification(DebugUtil.menuOrder());
    }

    public LiveData<Store> findStore(Activity activity) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        sessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMap(sessionId -> p2pClient.findStore(activity, sessionId))
                .flatMapObservable(storeManager::getStore)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public void onSuccessPayment(String orderUuid) {
        orderManager.getMenuOrderFromDisk(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(notificationHandler::requestOrderProgressNotification)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        p2pClient.clear();
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mark.zumo.client.core.p2p.P2pServer;
import com.mark.zumo.client.store.model.SessionManager;

import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by mark on 18. 6. 9.
 */
public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private final SessionManager sessionManager;

    private P2pServer p2pServer;
    private final CompositeDisposable compositeDisposable;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public void findCustomer(Activity activity) {
        sessionManager.getSessionStore()
                .map(store -> p2pServer = new P2pServer(activity, store))
                .flatMapObservable(P2pServer::findCustomer)
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(customerUuid -> Log.d(TAG, "findCustomer: " + customerUuid))
                .subscribe();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        if (p2pServer != null) {
            p2pServer.stopAdvertising();
        }
    }
}

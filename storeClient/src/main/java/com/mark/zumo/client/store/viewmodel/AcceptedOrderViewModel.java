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
public class AcceptedOrderViewModel extends AndroidViewModel {

    private static final String TAG = "AcceptedOrderViewModel";

    private final SessionManager sessionManager;

    private final CompositeDisposable compositeDisposable;

    public AcceptedOrderViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public void findCustomer(Activity activity) {
        sessionManager.getSessionStore()
                .map(store -> new P2pServer(activity, store))
                .flatMapObservable(P2pServer::findCustomer)
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(customerUuid -> Log.d(TAG, "findCustomer: " + customerUuid))
                .subscribe();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}

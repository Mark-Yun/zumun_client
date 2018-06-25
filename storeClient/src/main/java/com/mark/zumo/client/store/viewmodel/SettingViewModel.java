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
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.store.model.SessionManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 25.
 */
public class SettingViewModel extends AndroidViewModel {

    private SessionManager sessionManager;
    private CompositeDisposable compositeDisposable;

    public SettingViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;
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

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}

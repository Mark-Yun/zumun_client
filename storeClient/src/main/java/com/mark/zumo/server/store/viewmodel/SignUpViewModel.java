/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.server.store.model.SessionManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private SessionManager sessionManager;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        sessionManager = SessionManager.INSTANCE;
    }

    public LiveData<Boolean> isSessionValid() {
        MutableLiveData<Boolean> existSession = new MutableLiveData<>();

        sessionManager.isSessionValid()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(existSession::setValue)
                .subscribe();

        return existSession;
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.customer.model.CustomerSessionManager;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private final CustomerSessionManager customerSessionManager;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        customerSessionManager = CustomerSessionManager.INSTANCE;
    }

    public LiveData<Boolean> isSessionValid() {
        MutableLiveData<Boolean> existSession = new MutableLiveData<>();
        existSession.postValue(true);
        return existSession;
    }
}

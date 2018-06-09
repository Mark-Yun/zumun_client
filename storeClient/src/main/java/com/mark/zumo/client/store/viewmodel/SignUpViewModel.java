/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.mark.zumo.client.store.model.SessionManager;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private SessionManager sessionManager;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        sessionManager = SessionManager.INSTANCE;
    }

    public boolean isSessionValid() {
        return sessionManager.isSessionAvailable();
    }
}

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

import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.store.model.SessionManager;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        sessionManager = SessionManager.INSTANCE;
    }

    public LiveData<StoreUserSignupException> signUp(final String email,
                                                     final String password,
                                                     final String passwordConfirm,
                                                     final String name,
                                                     final String phoneNumber,
                                                     final String bankName,
                                                     final String backAccount,
                                                     final String backAccountUrl) {

        MutableLiveData<StoreUserSignupException> liveData = new MutableLiveData<>();

        try {
            StoreOwnerSignUpRequest request = new StoreOwnerSignUpRequest.Builder()
                    .setEmail(email)
                    .setPassword(password)
                    .setPasswordConfirm(passwordConfirm)
                    .setName(name)
                    .setPhoneNumber(phoneNumber)
                    .setBankName(bankName)
                    .setBankAccount(backAccount)
                    .setBankAccountScanUrl(backAccountUrl)
                    .build();
        } catch (StoreUserSignupException e) {
            liveData.setValue(e);
        }

        return liveData;
    }
}

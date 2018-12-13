/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.StoreSessionManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private final StoreSessionManager storeSessionManager;
    private final StoreUserManager storeUserManager;
    private final S3TransferManager s3TransferManager;

    private final CompositeDisposable disposables;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        storeSessionManager = StoreSessionManager.INSTANCE;
        storeUserManager = StoreUserManager.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;

        disposables = new CompositeDisposable();
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

            storeUserManager.signup(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(liveData::setValue)
                    .doOnSubscribe(disposables::add)
                    .subscribe();
        } catch (StoreUserSignupException e) {
            new Handler(Looper.getMainLooper()).post(() -> liveData.setValue(e));
        }

        return liveData;
    }


    public LiveData<String> uploadBankAccountScanImage(Activity activity, Uri uri) {
        MutableLiveData<String> liveData = new MutableLiveData<>();

        s3TransferManager.uploadBankScanImage(activity, uri)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }
}

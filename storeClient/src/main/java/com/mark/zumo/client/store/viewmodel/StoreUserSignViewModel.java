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
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.core.entity.user.store.StoreUserSession;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 7.
 */

public class StoreUserSignViewModel extends AndroidViewModel {

    private final StoreUserManager storeUserManager;
    private final S3TransferManager s3TransferManager;

    private final CompositeDisposable disposables;

    public StoreUserSignViewModel(@NonNull Application application) {
        super(application);

        storeUserManager = StoreUserManager.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<StoreUserSignupException> signUp(final String email, final String password,
                                                     final String passwordConfirm, final String phoneNumber) {

        MutableLiveData<StoreUserSignupException> liveData = new MutableLiveData<>();

        try {
            StoreOwnerSignUpRequest request = new StoreOwnerSignUpRequest.Builder()
                    .setEmail(email)
                    .setPassword(password)
                    .setPhoneNumber(phoneNumber)
                    .setPasswordConfirm(passwordConfirm)
                    .build();

            storeUserManager.signup(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(liveData::setValue)
                    .doOnError(throwable -> liveData.setValue(new StoreUserSignupException(StoreUserSignupErrorCode.SERVER_ERROR)))
                    .doOnSubscribe(disposables::add)
                    .subscribe();
        } catch (StoreUserSignupException e) {
            liveData.postValue(e);
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

    public LiveData<StoreUserSession> loginStoreUser(final String email, final String password,
                                                     final boolean isAutoLogin) {
        MutableLiveData<StoreUserSession> liveData = new MutableLiveData<>();

        storeUserManager.login(email, password, isAutoLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<Boolean> hasStoreUserSessionAsync() {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        storeUserManager.getStoreUserSessionAsync()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(storeUserSession -> liveData.setValue(storeUserSession != null))
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public boolean hasStoreUserSessionSync() {
        return storeUserManager.getStoreUserSessionSync() != null;
    }
}

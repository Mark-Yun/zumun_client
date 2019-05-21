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
import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInErrorCode;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreUserSignupException;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.store.model.StoreS3TransferManager;
import com.mark.zumo.client.store.model.StoreStoreManager;
import com.mark.zumo.client.store.model.StoreUserManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 7.
 */

public class StoreUserSignViewModel extends AndroidViewModel {

    private final StoreUserManager storeUserManager;
    private final StoreStoreManager storeStoreManager;
    private final StoreS3TransferManager storeS3TransferManager;

    private final CompositeDisposable disposables;

    public StoreUserSignViewModel(@NonNull Application application) {
        super(application);

        storeUserManager = StoreUserManager.INSTANCE;
        storeStoreManager = StoreStoreManager.INSTANCE;
        storeS3TransferManager = StoreS3TransferManager.INSTANCE;

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

        storeUserManager.getStoreUserSession()
                .map(storeUserSession -> storeUserSession.uuid)
                .flatMap(storeUserUuid -> storeS3TransferManager.uploadBankScanImage(activity, storeUserUuid, uri))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<StoreUserSignInErrorCode> loginStoreUser(final String email, final String password,
                                                             final boolean isAutoLogin) {
        MutableLiveData<StoreUserSignInErrorCode> liveData = new MutableLiveData<>();

        storeUserManager.signIn(email, password, isAutoLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<Boolean> hasStoreUserSessionAsync() {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        storeUserManager.getStoreUserSession()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(storeUserSession -> liveData.setValue(storeUserSession != null))
                .doOnComplete(() -> liveData.setValue(liveData.getValue() != null))
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public LiveData<Boolean> hasSessionStoreAsync() {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        storeStoreManager.getStoreSessionMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(sessionStore -> liveData.setValue(sessionStore != null))
                .doOnComplete(() -> liveData.setValue(liveData.getValue() != null))
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }
}

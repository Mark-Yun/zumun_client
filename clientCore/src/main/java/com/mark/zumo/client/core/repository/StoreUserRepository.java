/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.login.StoreUserSignInRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInResponse;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.database.AppDatabaseProvider;
import com.mark.zumo.client.core.database.dao.DiskRepository;
import com.mark.zumo.client.core.database.entity.SessionStore;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.database.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserContract;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserSession;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 12. 13.
 */
public enum StoreUserRepository {
    INSTANCE;

    private final DiskRepository diskRepository;

    StoreUserRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public Maybe<String> storeUserHandShake(String email) {
        return networkRepository().signInHandShake(email)
                .map(storeUserHandShakeResponse -> storeUserHandShakeResponse.publicKey);
    }

    public Flowable<StoreUserSession> getStoreUserSessionFlowable() {
        return diskRepository.getStoreUserSessionFlowable();
    }

    public Observable<StoreUserSession> getStoreUserSessionMaybe() {
        return diskRepository.getStoreUserSessionMaybe()
                .toObservable();
    }

    public Observable<StoreOwner> getStoreOwner(String storeUserUuid) {
        Maybe<StoreOwner> storeUserDB = diskRepository.getStoreOwner(storeUserUuid);
        Maybe<StoreOwner> storeUserApi = networkRepository().getStoreOwner(storeUserUuid)
                .doOnSuccess(diskRepository::insertStoreUser);

        return Maybe.merge(storeUserDB, storeUserApi)
                .distinctUntilChanged()
                .toObservable();
    }

    public Maybe<Store> getSessionStoreMaybe() {
        return diskRepository.getSessionStore()
                .map(Store::from);
    }

    public Maybe<StoreUserSignupErrorCode> creteStoreOwner(StoreOwnerSignUpRequest request) {
        return networkRepository().createStoreOwner(request)
                .map(storeOwnerSignUpResponse -> storeOwnerSignUpResponse.storeUserSignUpResponse)
                .map(StoreUserSignupErrorCode::valueOf);
    }

    public void putSessionHeader(Bundle bundle) {
        AppServerServiceProvider.INSTANCE.putSessionHeader(bundle);
    }

    public void clearSessionHeader() {
        AppServerServiceProvider.INSTANCE.clearSessionHeader();
    }

    public void removeStoreUserSession() {
        diskRepository.removeAllStoreUserSession();
    }

    public Maybe<StoreUserSignInResponse> loginStoreUser(final StoreUserSignInRequest request) {
        return networkRepository().storeUserLogin(request);
    }

    public void saveStoreUserSession(StoreUserSession storeUserSession) {
        if (storeUserSession != null) {
            diskRepository.insertStoreUserSession(storeUserSession);
        } else {
            diskRepository.removeAllStoreUserSession();
        }
    }

    public Observable<List<StoreUserContract>> getStoreUserContract(String storeUserUuid) {
        Maybe<List<StoreUserContract>> storeUserContractListApi = networkRepository().getStoreUserContractListByStoreUserUuid(storeUserUuid);
        Maybe<List<StoreUserContract>> storeUserContractListDB = diskRepository.getStoreUserContractListbyStoreUserUuid(storeUserUuid);

        return Maybe.merge(storeUserContractListDB, storeUserContractListApi)
                .distinctUntilChanged()
                .toObservable();
    }

    public Maybe<StoreOwner> updateStoreOwner(StoreOwner storeOwner) {
        return networkRepository().updateStoreOwner(storeOwner.uuid, storeOwner)
                .doOnSuccess(diskRepository::insertStoreOwner);
    }
}

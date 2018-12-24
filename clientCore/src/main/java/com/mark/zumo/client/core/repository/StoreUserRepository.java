/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.login.StoreUserLoginRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.entity.user.store.StoreUserSession;
import com.mark.zumo.client.core.security.SecurePreferences;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 12. 13.
 */
public enum StoreUserRepository {
    INSTANCE;

    private final SecurePreferences securePreferences;
    private final NetworkRepository networkRepository;
    private final DiskRepository diskRepository;

    StoreUserRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Maybe<String> storeUserHandShake(String email) {
        return networkRepository.signInHandShake(email)
                .map(storeUserHandShakeResponse -> storeUserHandShakeResponse.publicKey);
    }

    public Maybe<StoreUserSession> getStoreUserSession() {
        return diskRepository.getStoreUserSession();
    }

    public Maybe<StoreOwner> creteStoreOwner(StoreOwnerSignUpRequest request) {
        return networkRepository.createStoreOwner(request)
                .doOnSuccess(diskRepository::insertStoreOwner);
    }

    public Maybe<StoreUserSession> loginStoreUser(StoreUserLoginRequest request) {
        return networkRepository.storeUserLogin(request)
                .map(storeUserLoginResponse -> storeUserLoginResponse.sessionToken)
                .map(sessionToken -> new StoreUserSession.Builder()
                        .setEmail(request.email)
                        .setPassword(request.password)
                        .setToken(sessionToken)
                        .build());
    }

    public void saveStoreUserSession(StoreUserSession storeUserSession) {
        diskRepository.insertStoreUserSession(storeUserSession);
    }
}

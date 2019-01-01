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
import com.mark.zumo.client.core.entity.SessionStore;
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.entity.user.store.StoreUserContract;
import com.mark.zumo.client.core.entity.user.store.StoreUserSession;
import com.mark.zumo.client.core.security.SecurePreferences;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

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

    public Maybe<SessionStore> getSessionStore() {
        return diskRepository.getSessionStore();
    }

    public void saveSessionStore(SessionStore sessionStore) {
        diskRepository.insertSessionStore(sessionStore);
    }

    public void clearStoreUserSession() {
        diskRepository.storeUserSession();
    }

    public Maybe<StoreOwner> creteStoreOwner(StoreOwnerSignUpRequest request) {
        return networkRepository.createStoreOwner(request)
                .doOnSuccess(diskRepository::insertStoreOwner);
    }

    public Maybe<StoreUserSession> loginStoreUser(final StoreUserLoginRequest request) {
        return networkRepository.storeUserLogin(request)
                .map(storeUserLoginResponse -> new StoreUserSession.Builder()
                        .setEmail(request.email)
                        .setPassword(request.password)
                        .setUuid(storeUserLoginResponse.storeUserUuid)
                        .setToken(storeUserLoginResponse.sessionToken)
                        .build());
    }

    public void saveStoreUserSession(StoreUserSession storeUserSession) {
        diskRepository.insertStoreUserSession(storeUserSession);
    }

    public Observable<List<StoreUserContract>> getStoreUserContract(String storeUserUuid) {
        Maybe<List<StoreUserContract>> storeUserContractListApi = networkRepository.getStoreUserContractListByStoreUserUuid(storeUserUuid);
        Maybe<List<StoreUserContract>> storeUserContractListDB = diskRepository.getStoreUserContractListbyStoreUserUuid(storeUserUuid);

        return Maybe.merge(storeUserContractListDB, storeUserContractListApi)
                .distinctUntilChanged()
                .toObservable();
    }
}

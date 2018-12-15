/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserHandShakeRequest;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.security.SecurePreferences;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 12. 13.
 */
public enum UserRepository {
    INSTANCE;

    private final SecurePreferences securePreferences;
    private final NetworkRepository networkRepository;
    private final DiskRepository diskRepository;

    UserRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Maybe<String> getLoginToken(String email) {
        return networkRepository.handShake(new StoreUserHandShakeRequest(email))
                .map(storeUserHandShakeResponse -> storeUserHandShakeResponse.publicKey);
    }

    public Maybe<StoreOwner> creteStoreOwner(StoreOwnerSignUpRequest request) {
        return networkRepository.createStoreOwner(request)
                .doOnSuccess(diskRepository::insertStoreOwner);
    }
}

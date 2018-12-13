/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.StoreRepository;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreSessionManager {
    INSTANCE;

    private static final String TAG = "StoreSessionManager";

    private final SessionRepository sessionRepository;
    private final Maybe<StoreRepository> storeRepositoryMaybe;

    StoreSessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
        storeRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(StoreRepository::getInstance);
    }

    public Maybe<Store> getSessionStore() {
        return sessionRepository.getStoreFromCache()
                .flatMap(storeUuid ->
                        storeRepositoryMaybe.flatMap(storeRepository ->
                                storeRepository.getStoreFromDisk(storeUuid)
                                        .switchIfEmpty(sessionRepository.getStoreFromCache()
                                                .flatMap(storeRepository::getStoreFromApi))
                        )
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<SnsToken> registerTokenOnRefresh(Store store, String token) {
        SnsToken snsToken = new SnsToken(store.uuid, SnsToken.TokenType.ANDROID, token);
        return sessionRepository.registerSnsToken(snsToken)
                .subscribeOn(Schedulers.io());
    }
}

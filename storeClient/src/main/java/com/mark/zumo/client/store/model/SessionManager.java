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

public enum SessionManager {
    INSTANCE;

    private static final String TAG = "SessionManager";

    private final SessionRepository sessionRepository;
    private final StoreRepository storeRepository;

    SessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
        storeRepository = StoreRepository.INSTANCE;

        getSessionStore()
                .doOnSuccess(this::buildSessionHeader)
                .subscribe();
    }

    public Maybe<Store> getSessionStore() {
        return sessionRepository.getStoreFromCache()
                .flatMap(storeRepository::getStoreFromApi)
                .subscribeOn(Schedulers.io());
    }

    private void buildSessionHeader(Store store) {
        new SessionRepository.SessionBuilder()
                .put(SessionRepository.KEY_STORE_UUID, store.uuid)
                .build();
    }

    public Maybe<SnsToken> registerToken(Store store, String token) {
        SnsToken snsToken = new SnsToken(store.uuid, SnsToken.TokenType.ANDROID, token);
        return sessionRepository.createToken(snsToken)
                .subscribeOn(Schedulers.io());
    }
}

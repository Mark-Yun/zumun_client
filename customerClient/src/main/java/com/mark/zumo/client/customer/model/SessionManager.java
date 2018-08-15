/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.repository.SessionRepository;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    private static final String TAG = "SessionManager";

    private static final Object sessionHeaderLock = new Object();

    private final SessionRepository sessionRepository;
    private boolean isBuiltSessionHeader;

    SessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
    }

    public Maybe<GuestUser> getSessionUser() {
        return Maybe.fromCallable(sessionRepository::getGuestUserFromCache)
                .switchIfEmpty(sessionRepository.createGuestUser()
                        .map(sessionRepository::saveGuestUser)
                ).doOnSuccess(this::buildSessionHeader)
                .subscribeOn(Schedulers.io());
    }

    private void buildSessionHeader(GuestUser guestUser) {
        if (!isBuiltSessionHeader) {
            synchronized (sessionHeaderLock) {
                if (!isBuiltSessionHeader) {
                    new SessionRepository.SessionBuilder()
                            .put(SessionRepository.KEY_CUSTOMER_UUID, guestUser.uuid)
                            .build();
                    isBuiltSessionHeader = true;
                }
            }
        }
    }

    public Maybe<SnsToken> registerToken(GuestUser guestUser, String token) {
        SnsToken snsToken = new SnsToken(guestUser.uuid, SnsToken.TokenType.ANDROID, token);
        return sessionRepository.createToken(snsToken)
                .subscribeOn(Schedulers.io());
    }
}

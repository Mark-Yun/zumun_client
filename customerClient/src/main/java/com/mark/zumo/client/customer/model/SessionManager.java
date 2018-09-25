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

    private final SessionRepository sessionRepository;

    SessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
    }

    public Maybe<GuestUser> getSessionUser() {
        return sessionRepository.getSessionUser()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<SnsToken> registerToken(GuestUser guestUser, String token) {
        SnsToken snsToken = new SnsToken(guestUser.uuid, SnsToken.TokenType.ANDROID, token);
        return sessionRepository.registerSnsToken(snsToken)
                .subscribeOn(Schedulers.io());
    }
}

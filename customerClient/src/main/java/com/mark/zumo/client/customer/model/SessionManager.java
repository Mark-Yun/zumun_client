/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.repository.SessionRepository;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    public static final String TAG = "SessionManager";

    private SessionRepository sessionRepository;

    private GuestUser guestUser;

    SessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
        getSessionUser()
                .doOnSuccess(this::buildSessionHeader)
                .doOnSuccess(guestUser -> this.guestUser = guestUser)
                .subscribe();
    }

    public GuestUser getCurrentGuestUser() {
        return guestUser;
    }

    public Maybe<GuestUser> getSessionUser() {
        return Maybe.fromCallable(() -> sessionRepository.getGuestUserFromCache())
                .switchIfEmpty(sessionRepository.createGuestUser()
                        .map(sessionRepository::saveGuestUser)
                ).subscribeOn(Schedulers.io());
    }

    private void buildSessionHeader(GuestUser guestUser) {
        new SessionRepository.SessionBuilder()
                .put(SessionRepository.KEY_CUSTOMER_UUID, guestUser.uuid)
                .build();
    }
}

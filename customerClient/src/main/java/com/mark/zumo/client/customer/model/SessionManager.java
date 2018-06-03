/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.repository.SessionRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    public static final String TAG = "SessionManager";

    private SessionRepository sessionRepository;
    private NetworkRepository networkRepository;
    private AppServerServiceProvider appServerServiceProvider;

    SessionManager() {
        sessionRepository = SessionRepository.INSTANCE;
        appServerServiceProvider = AppServerServiceProvider.INSTANCE;
        networkRepository = appServerServiceProvider.networkRepository;

        getSessionId().subscribe();
    }

    public Maybe<String> getSessionId() {
        return Maybe.fromCallable(() -> sessionRepository.getGuestUserUuid())
                .switchIfEmpty(
                        networkRepository.createGuestUser()
                                .retryWhen(errors -> errors.flatMap(error -> Flowable.timer(3, TimeUnit.SECONDS)))
                                .retry(2)
                                .map(guestUser -> guestUser.uuid)
                                .doOnSuccess(this::getSessionId)
                                .doOnSuccess(sessionRepository::saveGuestUserUuid)
                ).subscribeOn(Schedulers.io());
    }

    private NetworkRepository getSessionId(final String guestUserUuid) {
        Bundle bundle = new Bundle();
        if (!guestUserUuid.isEmpty()) {
            bundle.putString(SessionRepository.KEY_GUEST_USER_UUID, guestUserUuid);
        }
        return appServerServiceProvider.buildSessionHeader(bundle);
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionRepository {
    INSTANCE;

    public static final String KEY_CUSTOMER_UUID = "customer_uuid";
    public static final String KEY_STORE_UUID = "store_uuid";

    private static final String TAG = "SessionRepository";

    private final SecurePreferences securePreferences;
    private final NetworkRepository networkRepository;

    private static final Object sessionHeaderLock = new Object();

    private boolean isBuiltSessionHeader;

    SessionRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
    }

    private GuestUser saveGuestUser(final GuestUser guestUser) {
        securePreferences.put(SessionRepository.KEY_CUSTOMER_UUID, guestUser.uuid);
        return guestUser;
    }

    public GuestUser getGuestUserFromCache() {
        try {
            String guestUserUuid = securePreferences.getString(SessionRepository.KEY_CUSTOMER_UUID);
            if (TextUtils.isEmpty(guestUserUuid)) {
                return null;
            }

            return new GuestUser(guestUserUuid);
        } catch (SecurePreferences.SecurePreferencesException e) {
            Log.e(TAG, "getGuestUserFromCache: ", e);
            return null;
        }
    }

    public Maybe<GuestUser> getSessionUser() {
        return Maybe.fromCallable(this::getGuestUserFromCache)
                .switchIfEmpty(createGuestUser())
                .doOnSuccess(this::buildGuestUserSessionHeader);
    }

    public Maybe<String> getStoreFromCache() {
        //TODO: remove test data
        return Maybe.fromCallable(DebugUtil::store)
                .map(store -> store.uuid);
    }

    public Maybe<GuestUser> createGuestUser() {
        return networkRepository.createGuestUser()
                .doOnSuccess(this::saveGuestUser)
                .retryWhen(errors -> errors.flatMap(error -> Flowable.timer(3, TimeUnit.SECONDS)))
                .retry(2);
    }

    private void buildGuestUserSessionHeader(GuestUser guestUser) {
        if (!isBuiltSessionHeader) {
            synchronized (sessionHeaderLock) {
                if (!isBuiltSessionHeader) {
                    new SessionBuilder()
                            .put(SessionRepository.KEY_CUSTOMER_UUID, guestUser.uuid)
                            .build();
                    isBuiltSessionHeader = true;
                }
            }
        }
    }

    public Maybe<SnsToken> createToken(SnsToken snsToken) {
        return networkRepository.createSnsToken(snsToken)
                .subscribeOn(Schedulers.io());
    }

    private static class SessionBuilder {
        private final Bundle bundle;

        private SessionBuilder() {
            bundle = new Bundle();
        }

        public SessionBuilder put(String key, String value) {
            bundle.putString(key, value);
            return this;
        }

        public NetworkRepository build() {
            AppServerServiceProvider.INSTANCE.buildPaymentService(bundle);
            return AppServerServiceProvider.INSTANCE.buildNetworkRepository(bundle);
        }
    }
}

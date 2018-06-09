/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionRepository {
    INSTANCE;


    public static final String KEY_CUSTOMER_UUID = "customer_uuid";
    public static final String KEY_STORE_UUID = "store_uuid";

    private static final String TAG = "SessionRepository";

    private SecurePreferences securePreferences;
    private NetworkRepository networkRepository;

    SessionRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
    }

    public GuestUser saveGuestUser(final GuestUser guestUser) {
        securePreferences.put(SessionRepository.KEY_CUSTOMER_UUID, guestUser.uuid);
        return guestUser;
    }

    public GuestUser getGuestUserFromCache() {
        String guestUserUuid = securePreferences.getString(SessionRepository.KEY_CUSTOMER_UUID);
        return new GuestUser(guestUserUuid);
    }

    public Maybe<Store> getStoreFromCache() {
        //TODO: remove test data
        return Maybe.fromCallable(DebugUtil::store);
    }

    public Maybe<GuestUser> createGuestUser() {
        return networkRepository.createGuestUser()
                .retryWhen(errors -> errors.flatMap(error -> Flowable.timer(3, TimeUnit.SECONDS)))
                .retry(2);
    }

    public static class SessionBuilder {
        private Bundle bundle;

        public SessionBuilder() {
            bundle = new Bundle();
        }

        public SessionBuilder put(String key, String value) {
            bundle.putString(key, value);
            return this;
        }

        public NetworkRepository build() {
            return AppServerServiceProvider.INSTANCE.buildNetworkRepository(bundle);
        }
    }
}

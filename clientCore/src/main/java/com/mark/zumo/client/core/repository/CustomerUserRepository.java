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
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.SecurePreferences;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum CustomerUserRepository {
    INSTANCE;


    private static final String TAG = "CustomerUserRepository";
    private static final String KEY_SESSION_GUEST_USER_UUID = "guest_user_uuid";

    private final SecurePreferences securePreferences;
    private final DiskRepository diskRepository;

    CustomerUserRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public void saveGuestUser(final GuestUser guestUser) {
        securePreferences.put(KEY_SESSION_GUEST_USER_UUID, guestUser.uuid);
    }

    public Maybe<GuestUser> getGuestUserSession() {
        return Maybe.fromCallable(() -> {
            try {
                String guestUserUuid = securePreferences.getString(KEY_SESSION_GUEST_USER_UUID);
                if (TextUtils.isEmpty(guestUserUuid)) {
                    return null;
                }

                return new GuestUser(guestUserUuid);
            } catch (SecurePreferences.SecurePreferencesException e) {
                Log.e(TAG, "getCustomerFromSecuredRepository: ", e);
                return null;
            }
        });
    }

    public Maybe<GuestUser> createGuestUser() {
        return networkRepository().createGuestUser()
                .doOnSuccess(this::saveGuestUser)
                .flatMap(this::registerSnsTokenOnCreateUser);
    }

    private Maybe<GuestUser> registerSnsTokenOnCreateUser(GuestUser guestUser) {
        return diskRepository.getLatestSnsToken()
                .map(snsToken -> new SnsToken(guestUser.uuid, snsToken.tokenType, snsToken.tokenValue))
                .flatMap(this::registerSnsToken)
                .map(x -> guestUser);
    }

    public void putSessionHeader(Bundle bundle) {
        AppServerServiceProvider.INSTANCE.putSessionHeader(bundle);
    }

    public void clearSessionHeader(String key) {
        AppServerServiceProvider.INSTANCE.clearSessionHeader(key);
    }

    public Maybe<SnsToken> registerSnsToken(SnsToken snsToken) {
        return networkRepository().createSnsToken(snsToken)
                .doOnSuccess(diskRepository::insertSnsToken)
                .subscribeOn(Schedulers.io());
    }
}

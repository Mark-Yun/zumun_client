/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.user.GuestUser;
import com.mark.zumo.client.core.repository.CustomerUserRepository;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum CustomerSessionManager {

    INSTANCE;

    private static final String TAG = "CustomerSessionManager";

    private static final String KEY_CUSTOMER_UUID = "customer_uuid";

    private final CustomerUserRepository customerUserRepository;

    private GuestUser guestUser;

    CustomerSessionManager() {
        customerUserRepository = CustomerUserRepository.INSTANCE;
    }

    public Maybe<GuestUser> getSessionUser() {
        return customerUserRepository.getGuestUserSession()
                .switchIfEmpty(customerUserRepository.createGuestUser())
                .flatMap(this::setGuestUserSession)
                .subscribeOn(Schedulers.io());
    }

    private Maybe<GuestUser> setGuestUserSession(GuestUser guestUser) {
        return Maybe.just(guestUser)
                .doOnSuccess(customerUserRepository::saveGuestUser)
                .doOnSuccess(guestUserSession -> this.guestUser = guestUserSession)
                .doOnSuccess(guestUserSession -> customerUserRepository.putSessionHeader(buildStoreUserSessionHeader()))
                .doOnSuccess(x -> registerToken())
                .map(x -> guestUser)
                .subscribeOn(Schedulers.io());
    }

    public void registerToken() {
        if (guestUser == null) {
            Log.w(TAG, "registerToken: guestUserSession is not created yet");
            return;
        }

        Maybe.just(guestUser)
                .flatMap(this::createSnsToken)
                .flatMap(customerUserRepository::registerSnsToken)
                .doOnSuccess(snsToken -> Log.d(TAG, "registerToken: snsToken=" + snsToken))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Maybe<String> getFCMToken() {
        return Maybe.create((MaybeOnSubscribe<String>) emitter ->
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getFCMToken: getInstanceId failed", task.getException());
                                emitter.onComplete();
                                return;
                            }

                            InstanceIdResult result = task.getResult();
                            if (result == null) {
                                Log.w(TAG, "getFCMToken: getResult failed");
                                emitter.onComplete();
                                return;
                            }

                            String token = result.getToken();
                            Log.d(TAG, "getFCMToken: token=" + token);
                            emitter.onSuccess(token);
                            emitter.onComplete();
                        })
        ).subscribeOn(Schedulers.io());
    }

    private Maybe<SnsToken> createSnsToken(GuestUser guestUser) {
        return getFCMToken()
                .map(token -> new SnsToken(guestUser.uuid, SnsToken.TokenType.ANDROID, token));
    }

    private Bundle buildStoreUserSessionHeader() {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_CUSTOMER_UUID, guestUser != null ? guestUser.uuid : "");

        return bundle;
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mark.zumo.client.core.appserver.request.login.StoreUserSignInRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInErrorCode;
import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInResponse;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreUserSignupException;
import com.mark.zumo.client.core.entity.SessionStore;
import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.entity.user.store.StoreUserContract;
import com.mark.zumo.client.core.entity.user.store.StoreUserSession;
import com.mark.zumo.client.core.repository.StoreRepository;
import com.mark.zumo.client.core.repository.StoreUserRepository;
import com.mark.zumo.client.core.security.EncryptionUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreUserManager {
    INSTANCE;

    private static final String TAG = "StoreUserManager";

    private static final String STORE_USER_SESSION_HEADER_KEY = "store_user_session_token";
    private static final String SESSION_STORE_KEY = "store_uuid";
    private static final String PASSWORD_DIGEST_ALGORITHM = "MD5";

    private final StoreUserRepository storeUserRepository;
    private final StoreRepository storeRepository;

    @Nullable
    private StoreUserSession storeUserSession;
    @Nullable
    private SessionStore storeSession;

    private Subject<Store> sessionStoreSubject;
    private Subject<StoreUserSession> storeUserSessionSubject;

    StoreUserManager() {
        storeUserRepository = StoreUserRepository.INSTANCE;
        storeRepository = StoreRepository.INSTANCE;
    }

    private static String passwordDigest(final String source) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(PASSWORD_DIGEST_ALGORITHM);
            digest.update(source.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String hexedString = Integer.toHexString(0xFF & aMessageDigest);
                while (hexedString.length() < 2) {
                    hexedString = "0" + hexedString;
                }
                hexString.append(hexedString);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "passwordDigest: ", e);
        }
        return "";
    }

    public Maybe<StoreUserSignupException> signup(StoreOwnerSignUpRequest request) {
        return storeUserRepository.storeUserHandShake(request.email)
                .map(publicKey -> {
                    request.password = EncryptionUtil.encryptRSA(publicKey, passwordDigest(request.password));
                    return request;
                }).flatMap(storeUserRepository::creteStoreOwner)
                .map(StoreUserSignupException::new)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreUserSignInErrorCode> signIn(final String email, final String password, final boolean isAutoLogin) {
        return storeUserRepository.storeUserHandShake(email)
                .map(publicKey -> EncryptionUtil.encryptRSA(publicKey, passwordDigest(password)))
                .map(encryptedPassword -> new StoreUserSignInRequest.Builder()
                        .setEmail(email)
                        .setPassword(encryptedPassword)
                        .build())
                .flatMap(storeUserRepository::loginStoreUser)
                .doOnSuccess(response -> onSignInSucceed(email, password, isAutoLogin, response))
                .map(storeUserSignInResponse -> storeUserSignInResponse.storeUserSignInResponse)
                .map(StoreUserSignInErrorCode::valueOf)
                .subscribeOn(Schedulers.io());
    }

    @Nullable
    public StoreUserSession getStoreUserSessionSync() {
        return storeUserSession;
    }

    public Maybe<StoreUserSession> getStoreUserSessionAsync() {
        return storeUserRepository.getStoreUserSessionMaybe()
                .firstElement()
                .doOnSuccess(this::setStoreUserSession)
                .subscribeOn(Schedulers.io());
    }

    @Nullable
    public SessionStore getSessionStoreSync() {
        return storeSession;
    }

    public Maybe<Store> getSessionStoreAsync() {
        return storeUserRepository.getSessionStoreMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Store> getSessionStoreAsyncFlowable() {
        return storeUserRepository.getSessionStoreFlowable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> setSessionStore(Store store) {
        return Maybe.just(SessionStore.from(store))
                .doOnSuccess(storeUserRepository::saveSessionStore)
                .doOnSuccess(sessionStore -> this.storeSession = sessionStore)
                .doOnSuccess(sessionStore -> storeUserRepository.putSessionHeader(buildStoreUserSessionHeader()))
                .doOnSuccess(sessionStore -> registerToken())
                .map(x -> store)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreUserContract>> getStoreUserContract(String storeUserUuid) {
        return storeUserRepository.getStoreUserContract(storeUserUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<StoreOwner> getStoreOwner(String storeUserUuid) {
        return storeUserRepository.getStoreOwner(storeUserUuid)
                .subscribeOn(Schedulers.io());
    }

    public void signOut() {
        setStoreUserSession(null);
    }

    private void onSignInSucceed(final String email, final String password, final boolean isAutoSignIn,
                                 final StoreUserSignInResponse storeUserSignInResponse) {
        StoreUserSession storeUserSession = new StoreUserSession.Builder()
                .setEmail(email)
                .setPassword(password)
                .setUuid(storeUserSignInResponse.storeUserUuid)
                .setToken(storeUserSignInResponse.sessionToken)
                .build();
        setStoreUserSession(storeUserSession);

        if (isAutoSignIn) {
            storeUserRepository.saveStoreUserSession(storeUserSession);
        }
    }

    private void setStoreUserSession(@Nullable final StoreUserSession storeUserSession) {

        this.storeUserSession = storeUserSession;
        storeUserRepository.putSessionHeader(buildStoreUserSessionHeader());
        Maybe.fromAction(() -> storeUserRepository.saveStoreUserSession(storeUserSession))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Bundle buildStoreUserSessionHeader() {
        Bundle bundle = new Bundle();

        bundle.putString(StoreUserSession.Schema.token, storeUserSession != null ? storeUserSession.token : "");
        bundle.putString(StoreUserSession.Schema.email, storeUserSession != null ? storeUserSession.email : "");
        bundle.putString(Store.Schema.uuid, storeSession != null ? storeSession.uuid : "");

        return bundle;
    }

    public void registerToken() {
        if (storeSession == null) {
            Log.w(TAG, "registerToken: storeSession is not created yet");
            return;
        }

        Maybe.just(storeSession)
                .flatMap(this::createSnsToken)
                .flatMap(storeUserRepository::registerSnsToken)
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
                            emitter.onSuccess(token);
                            emitter.onComplete();
                        })
        ).subscribeOn(Schedulers.io());
    }

    private Maybe<SnsToken> createSnsToken(SessionStore sessionStore) {
        return getFCMToken()
                .map(token -> new SnsToken(sessionStore.uuid, SnsToken.TokenType.ANDROID, token));
    }

    public Maybe<Store> updateSessionStoreName(String newName) {

        return storeUserRepository.getSessionStoreMaybe()
                .map(store -> Store.Builder.from(store)
                        .setName(newName)
                        .build())
                .flatMap(storeRepository::updateStore)
                .flatMap(this::setSessionStore)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateSessionStoreLocation(LatLng latLng) {

        return storeUserRepository.getSessionStoreMaybe()
                .map(store -> Store.Builder.from(store)
                        .setLatitude(latLng.latitude)
                        .setLongitude(latLng.longitude)
                        .build())
                .flatMap(storeRepository::updateStore)
                .flatMap(this::setSessionStore)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateSessionStoreCoverImageUrl(String coverImageUrl) {
        return storeUserRepository.getSessionStoreMaybe()
                .map(store -> Store.Builder.from(store)
                        .setCoverImageUrl(coverImageUrl)
                        .build())
                .flatMap(storeRepository::updateStore)
                .flatMap(this::setSessionStore)
                .subscribeOn(Schedulers.io());

    }

    public Maybe<Store> updateSessionStoreThumbnailImageUrl(String thumbnailImageUrl) {
        return storeUserRepository.getSessionStoreMaybe()
                .map(store -> Store.Builder.from(store)
                        .setThumbnailUrl(thumbnailImageUrl)
                        .build())
                .flatMap(storeRepository::updateStore)
                .flatMap(this::setSessionStore)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreOwner> updateStoreOwnerBank(String storeUserUuid, String bankCode, String bankAccountNumber) {
        return getStoreOwner(storeUserUuid)
                .lastElement()
                .map(StoreOwner.Builder::from)
                .map(builder -> builder.setBankCode(bankCode))
                .map(builder -> builder.setBankAccountNumber(bankAccountNumber))
                .map(StoreOwner.Builder::build)
                .flatMap(storeUserRepository::updateStoreOwner)
                .subscribeOn(Schedulers.io());
    }
}

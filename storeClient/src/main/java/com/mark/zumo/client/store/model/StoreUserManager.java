/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.util.Base64Utils;
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
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.StoreUserRepository;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreUserManager {
    INSTANCE;

    private static final String TAG = "StoreUserManager";

    private static final String STORE_USER_SESSION_HEADER_KEY = "store_user_session_token";
    private static final String SESSION_STORE_KEY = "store_uuid";

    private final StoreUserRepository storeUserRepository;
    private final SessionRepository sessionRepository;

    @Nullable
    private StoreUserSession storeUserSession;
    @Nullable
    private Store sessionStore;

    StoreUserManager() {
        storeUserRepository = StoreUserRepository.INSTANCE;
        sessionRepository = SessionRepository.INSTANCE;
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String hexedString = Integer.toHexString(0xFF & aMessageDigest);
                while (hexedString.length() < 2)
                    hexedString = "0" + hexedString;
                hexString.append(hexedString);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private PublicKey createPublicKey(final String publicKeyString) {
        String refinedPublicKey = publicKeyString;

        refinedPublicKey = refinedPublicKey.replace("-----BEGIN PUBLIC KEY-----\n", "");
        refinedPublicKey = refinedPublicKey.replace("-----END PUBLIC KEY-----", "");

        byte[] decodedPublicKey = Base64Utils.decode(refinedPublicKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.e(TAG, "createPublicKey: exception occurred", e);
            return null;
        }
    }

    @Nullable
    private String RSAEncrypt(final String publicKeyString, final String password) {
        try {
            PublicKey publicKey = createPublicKey(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(password.getBytes());

            return Base64Utils.encode(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "RSAEncrypt: ", e);
            return null;
        }
    }

    public Maybe<StoreUserSignupException> signup(StoreOwnerSignUpRequest request) {
        return storeUserRepository.storeUserHandShake(request.email)
                .map(publicKey -> {
                    request.password = RSAEncrypt(publicKey, md5(request.password));
                    return request;
                }).flatMap(storeUserRepository::creteStoreOwner)
                .map(StoreUserSignupException::new)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreUserSignInErrorCode> signIn(final String email, final String password, final boolean isAutoLogin) {
        return storeUserRepository.storeUserHandShake(email)
                .map(publicKey -> RSAEncrypt(publicKey, md5(password)))
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
        return storeUserRepository.getStoreUserSession()
                .doOnSuccess(this::setStoreUserSession)
                .subscribeOn(Schedulers.io());
    }

    @Nullable
    public Store getSessionStoreSync() {
        return sessionStore;
    }

    public Maybe<? extends Store> getSessionStoreAsync() {
        return storeUserRepository.getSessionStore()
                .doOnSuccess(sessionStore -> Log.d(TAG, "getSessionStoreAsync: " + sessionStore))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<? extends Store> setSessionStore(Store store) {
        Log.d(TAG, "setSessionStore: " + store);
        return Maybe.just(SessionStore.from(store))
                .doOnSuccess(storeUserRepository::saveSessionStore)
                .doOnSuccess(sessionStore -> this.sessionStore = sessionStore)
                .doOnSuccess(sessionStore -> sessionRepository.putSessionHeader(buildStoreUserSessionHeader()))
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
        sessionRepository.putSessionHeader(buildStoreUserSessionHeader());
        Maybe.fromAction(() -> storeUserRepository.saveStoreUserSession(storeUserSession))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Bundle buildStoreUserSessionHeader() {
        Bundle bundle = new Bundle();

        bundle.putString(STORE_USER_SESSION_HEADER_KEY, storeUserSession != null ? storeUserSession.token : "");
        bundle.putString(SESSION_STORE_KEY, sessionStore != null ? sessionStore.uuid : "");

        return bundle;
    }

    public Maybe<SnsToken> registerTokenOnRefresh(Store store, String token) {
        SnsToken snsToken = new SnsToken(store.uuid, SnsToken.TokenType.ANDROID, token);
        return sessionRepository.registerSnsToken(snsToken)
                .subscribeOn(Schedulers.io());
    }
}

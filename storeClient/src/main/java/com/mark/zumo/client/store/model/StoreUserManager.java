/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.google.android.gms.common.util.Base64Utils;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.core.repository.UserRepository;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.reactivex.Maybe;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreUserManager {
    INSTANCE;

    private static final String TAG = "StoreUserManager";

    private final UserRepository userRepository;

    StoreUserManager() {
        userRepository = UserRepository.INSTANCE;
    }


    public Maybe<StoreUserSignupException> signup(StoreOwnerSignUpRequest request) {
        return userRepository.getLoginToken(request.email)
                .map(publicKey -> {
                    request.password = RSAEncrypt(publicKey, request.password);
                    return request;
                }).flatMap(userRepository::creteStoreOwner)
                .map(storeOwner -> new StoreUserSignupException(StoreUserSignupErrorCode.SUCCESS))
                .subscribeOn(Schedulers.io());
    }

    private PublicKey getPublicKey(final String publicKeyString) {
        String refinedPublicKey = publicKeyString;

        refinedPublicKey = refinedPublicKey.replace("-----BEGIN PUBLIC KEY-----\n", "");
        refinedPublicKey = refinedPublicKey.replace("-----END PUBLIC KEY-----", "");

        Log.d(TAG, "getPublicKey: refinedPublicKey=" + refinedPublicKey);
        byte[] decodedPublicKey = Base64Utils.decode(refinedPublicKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.e(TAG, "getPublicKey: exception occurred", e);
            return null;
        }
    }

    @Nullable
    private String RSAEncrypt(final String publicKeyString, final String password) {
        try {
            PublicKey publicKey = getPublicKey(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            String encryptedString = Base64Utils.encode(encryptedBytes);

            Log.d(TAG, "RSAEncrypt: encryptedString=" + encryptedString);
            return encryptedString;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "RSAEncrypt: ", e);
            return null;
        }
    }


}

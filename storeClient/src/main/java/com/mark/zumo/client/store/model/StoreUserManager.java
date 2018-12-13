/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

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
        return userRepository.getLoginToken()
                .flatMap(publicKey -> encryptStoreOwnerSignUpRequest(publicKey, request))
                .flatMap(userRepository::creteStoreOwner)
                .map(storeOwner -> new StoreUserSignupException(StoreUserSignupErrorCode.SUCCESS))
                .subscribeOn(Schedulers.io());
    }

    private Maybe<String> encryptStoreOwnerSignUpRequest(String encryptKey, StoreOwnerSignUpRequest request) {
        return Maybe.fromCallable(() -> RSAEncrypt(encryptKey, request.toJson()))
                .map(String::new)
                .subscribeOn(Schedulers.computation());
    }

    @Nullable
    private byte[] RSAEncrypt(final String publicKeyString, final String plain) {
        try {
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
            System.out.println("EEncrypted?????" + new String(encryptedBytes));
            return encryptedBytes;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            Log.e(TAG, "RSAEncrypt: ", e);
            return null;
        }
    }

    private PublicKey getPublicKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getBytes());
        return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
    }
}

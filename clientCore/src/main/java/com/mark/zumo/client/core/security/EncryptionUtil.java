/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.security;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.util.Base64Utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by mark on 18. 5. 7.
 */

public class EncryptionUtil {

    private static final String TAG = "EncryptionUtil";

    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";

    private static PublicKey convertPublicKey(final String publicKeyString) {
        String refinedPublicKey = publicKeyString;

        refinedPublicKey = refinedPublicKey.replace(BEGIN_PUBLIC_KEY, "");
        refinedPublicKey = refinedPublicKey.replace(END_PUBLIC_KEY, "");

        byte[] decodedPublicKey = Base64Utils.decode(refinedPublicKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Log.e(TAG, "convertPublicKey: exception occurred", e);
            return null;
        }
    }

    @Nullable
    public static String encryptRSA(final String publicKeyString, final String content) {
        try {
            PublicKey publicKey = convertPublicKey(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(content.getBytes());

            return Base64Utils.encode(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "encryptRSA: ", e);
            return null;
        }
    }

    public static String decryptRSA(final PrivateKey privateKey, final String content) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = cipher.doFinal(content.getBytes());
            return new String(decodedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                BadPaddingException | IllegalBlockSizeException e) {
            Log.e(TAG, "decryptRSA: ", e);
            return "";
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(spec);
            return keyGen.generateKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            Log.e(TAG, "generateKeyPair: ", e);
            return null;
        }
    }

    public static String convertPublicKey(PublicKey publicKey) {
        String publicKeyContent = new String(Base64.encode(publicKey.getEncoded(), 0));
        return BEGIN_PUBLIC_KEY
                .concat(publicKeyContent)
                .concat(END_PUBLIC_KEY);
    }
}

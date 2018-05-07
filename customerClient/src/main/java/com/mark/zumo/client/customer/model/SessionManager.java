package com.mark.zumo.client.customer.model;

import android.content.Context;
import android.util.Log;

import com.mark.zumo.client.core.preference.SecurePreferences;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.UserRepository;
import com.mark.zumo.client.customer.CustomerClientApp;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    public static final String TAG = "SessionManager";
    public static final String PREFERENCE_NAME = "b77eafg23ad12`49ab-422dv84df8-#asd123hsdhsdfgQFWQ3.eb8c";
    public static final String SECURE_KEY = "5361a11b-615c-41SDFGSE23dgwasf3-6235123412b-e2c3790ada14";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_PASSWORD = "password";
    private Context context;

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SecurePreferences securePreferences;

    SessionManager() {
        context = CustomerClientApp.getContext();

        sessionRepository = SessionRepository.from(context);
        userRepository = UserRepository.from(context);
        securePreferences = new SecurePreferences(context, PREFERENCE_NAME, SECURE_KEY, true);
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] getKey() {
        try {
            byte[] keyStart = "this is a key".getBytes();
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            return skey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "getKey: ", e);
        }
        return "FAILED".getBytes();
    }

    public void saveToCache(String email, String password, byte[] key) throws Exception {
        securePreferences.put(PREF_KEY_EMAIL, email);

        byte[] encryptedPassword = encrypt(key, password.getBytes());
        securePreferences.put(PREF_KEY_PASSWORD, new String(encryptedPassword));
    }

    public Single<Boolean> isSessionValid() {
        return Single.create(e -> {

        });
    }
}

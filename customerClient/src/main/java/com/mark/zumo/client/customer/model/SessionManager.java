package com.mark.zumo.client.customer.model;

import android.content.Context;

import com.mark.zumo.client.core.exception.NotSignedUpException;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.UserRepository;
import com.mark.zumo.client.core.security.EncryptionUtil;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.customer.CustomerClientApp;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    public static final String TAG = "SessionManager";
    private Context context;

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SecurePreferences securePreferences;

    SessionManager() {
        context = CustomerClientApp.getContext();

        sessionRepository = SessionRepository.from(context);
        userRepository = UserRepository.from(context);
        securePreferences = new SecurePreferences(context, EncryptionUtil.PREFERENCE_NAME, EncryptionUtil.SECURE_KEY, true);
    }

    public void saveToCache(String email, String password, byte[] key) throws Exception {
        securePreferences.put(EncryptionUtil.PREF_KEY_EMAIL, email);
        securePreferences.put(EncryptionUtil.PREF_KEY_PKEY, new String(key));

        byte[] encryptedPassword = EncryptionUtil.encrypt(key, password.getBytes());
        securePreferences.put(EncryptionUtil.PREF_KEY_PASSWORD, new String(encryptedPassword));
    }

    public Single<String> requestSessionId() {

        return Single.create(e -> {
            if (!securePreferences.containsKey(EncryptionUtil.PREF_KEY_EMAIL))
                e.onError(new NotSignedUpException());

            if (!securePreferences.containsKey(EncryptionUtil.PREF_KEY_PASSWORD))
                e.onError(new NotSignedUpException());

            if (!securePreferences.containsKey(EncryptionUtil.PREF_KEY_PKEY))
                e.onError(new NotSignedUpException());

            String email = securePreferences.getString(EncryptionUtil.PREF_KEY_EMAIL);
            String rawPassword = securePreferences.getString(EncryptionUtil.PREF_KEY_PASSWORD);
            String pKey = securePreferences.getString(EncryptionUtil.PREF_KEY_PKEY);
            String password = new String(EncryptionUtil.decrypt(pKey.getBytes(), rawPassword.getBytes()));

            sessionRepository.requestSessionId(email, password)
                    .doOnSuccess(e::onSuccess)
                    .doOnError(e::onError)
                    .subscribe();
        });
    }

    public Single<Boolean> isSessionValid() {
        return Single.create(e -> {

        });
    }
}

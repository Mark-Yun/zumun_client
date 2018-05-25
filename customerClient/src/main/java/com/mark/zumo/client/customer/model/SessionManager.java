package com.mark.zumo.client.customer.model;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.UserRepository;
import com.mark.zumo.client.core.security.EncryptionContract;
import com.mark.zumo.client.core.security.EncryptionUtil;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.core.util.context.ContextHolder;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {

    INSTANCE;

    public static final String TAG = "SessionManager";

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SecurePreferences securePreferences;
    private NetworkRepository networkRepository;
    private AppServerServiceProvider appServerServiceProvider;

    SessionManager() {
        Context context = ContextHolder.getContext();

        sessionRepository = SessionRepository.from(context);
        userRepository = UserRepository.from(context);
        appServerServiceProvider = AppServerServiceProvider.INSTANCE;

        buildSessionHeader();
    }

    private void buildSessionHeader() {
        sessionRepository.getSessionId().doOnSuccess(appServerServiceProvider::buildSessionHeader);
    }

    public void saveToCache(String email, String password, byte[] key) throws Exception {
        securePreferences.put(EncryptionContract.PREF_KEY_EMAIL, email);
        securePreferences.put(EncryptionContract.PREF_KEY_KEY, new String(key));

        byte[] encryptedPassword = EncryptionUtil.encrypt(key, password.getBytes());
        securePreferences.put(EncryptionContract.PREF_KEY_PASSWORD, new String(encryptedPassword));
    }

    public Single<Boolean> isSessionValid() {
        return Single.create(e -> {
            e.onSuccess(false);
        });
    }

}

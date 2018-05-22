package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.SecurePreferences;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public class SessionRepository {

    public static final String KEY_GUEST_USER_UUID = "guest_user_uuid";
    public static final String TAG = "SessionRepository";
    private volatile static SessionRepository instance;

    private AppDatabase database;
    private SecuredRepository securedRepository;

    private SessionRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
        securedRepository = SecuredRepository.INSTANCE;
    }

    public static SessionRepository from(Context context) {
        if (instance == null) {
            synchronized (SessionRepository.class) {
                if (instance == null) instance = new SessionRepository(context);
            }
        }
        return instance;
    }

    AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }

    private Single<String> acquireGuestUserUuid() {
        Log.d(TAG, "acquireGuestUserUuid: ");

        return Single.zip(securedRepository.securePreferences()
                , appServerService().createGuestUser().map(GuestUser::getUuid)
                , this::saveGuestUserUuid);
    }

    private String saveGuestUserUuid(final SecurePreferences securePreferences, final String uuid) {
        Log.d(TAG, "saveGuestUserUuid: menu_uuid-" + uuid);
        securePreferences.put(SessionRepository.KEY_GUEST_USER_UUID, uuid);
        return uuid;
    }

    public Single<String> getSessionId() {
        return securedRepository.securePreferences()
                .flatMapMaybe(this::getGuestUserUuid)
                .doOnComplete(this::acquireGuestUserUuid)
                .toSingle();
    }

    private Maybe<String> getGuestUserUuid(final SecurePreferences securePreferences) {
        return Maybe.create(e -> {
            String guestUserUuid = securePreferences.getString(SessionRepository.KEY_GUEST_USER_UUID);
            if (guestUserUuid == null || guestUserUuid.isEmpty()) {
                e.onComplete();
            } else {
                e.onSuccess(guestUserUuid);
            }
        });
    }
}

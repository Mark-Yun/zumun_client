package com.mark.zumo.client.core.repository;

import android.util.Log;

import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.SecurePreferences;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionRepository {
    INSTANCE;

    public static final String KEY_GUEST_USER_UUID = "guest_user_uuid";
    public static final String TAG = "SessionRepository";

    private SecurePreferences securePreferences;

    SessionRepository() {
        securePreferences = SecuredRepository.INSTANCE.securePreferences();
    }

    public void saveGuestUser(final GuestUser guestUser) {
        securePreferences.put(SessionRepository.KEY_GUEST_USER_UUID, guestUser.uuid);
        Log.d(TAG, "saveGuestUser: menu_uuid-" + guestUser.uuid);
    }

    public String getGuestUserUuid() {
        return securePreferences.getString(SessionRepository.KEY_GUEST_USER_UUID);
    }
}

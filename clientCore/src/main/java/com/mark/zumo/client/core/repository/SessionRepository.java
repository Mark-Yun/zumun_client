/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

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

    public void saveGuestUserUuid(final String guestUserUuid) {
        securePreferences.put(SessionRepository.KEY_GUEST_USER_UUID, guestUserUuid);
    }

    public String getGuestUserUuid() {
        return securePreferences.getString(SessionRepository.KEY_GUEST_USER_UUID);
    }
}

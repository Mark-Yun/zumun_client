/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.security.EncryptionContract;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 5. 21.
 */
public enum SecuredRepository {
    INSTANCE;

    private final Object securePrefLock = new Object();
    private SecurePreferences securePreferences;

    public SecurePreferences securePreferences() {
        if (securePreferences == null) {
            synchronized (securePrefLock) {
                if (securePreferences == null) {
                    securePreferences = new SecurePreferences(ContextHolder.getContext(),
                            EncryptionContract.PREFERENCE_NAME,
                            EncryptionContract.SECURE_KEY,
                            true);
                }
            }
        }
        return securePreferences;
    }
}

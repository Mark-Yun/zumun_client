/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.app.fcm;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mark.zumo.client.store.model.SessionManager;

/**
 * Created by mark on 18. 6. 8.
 */
public class StoreFcmIdService extends FirebaseInstanceIdService {
    private static final String TAG = "StoreFcmIdService";

    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        sessionManager = SessionManager.INSTANCE;

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        registerToken(refreshedToken);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        registerToken(refreshedToken);

    }

    private void registerToken(final String refreshedToken) {
        if (TextUtils.isEmpty(refreshedToken)) {
            return;
        }

        sessionManager.getSessionStore()
                .flatMap(store -> sessionManager.registerToken(store, refreshedToken))
                .doOnSuccess(snsToken -> Log.d(TAG, "onTokenRefresh: updated Success-" + snsToken))
                .subscribe();
    }
}

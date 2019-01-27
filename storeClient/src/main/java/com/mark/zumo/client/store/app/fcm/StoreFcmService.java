/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.app.fcm;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mark.zumo.client.store.model.StoreUserManager;

import java.util.Map;

/**
 * Created by mark on 18. 6. 8.
 */
public class StoreFcmService extends FirebaseMessagingService {
    private final static String TAG = "StoreFcmService";

    private StoreMessageHandler storeMessageHandler;
    private StoreUserManager storeUserManager;

    @Override
    public void onCreate() {
        super.onCreate();

        storeMessageHandler = StoreMessageHandler.INSTANCE;
        storeUserManager = StoreUserManager.INSTANCE;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
            storeMessageHandler.handleMessage(data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(final String token) {
        Log.d(TAG, "onNewToken: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (TextUtils.isEmpty(token)) {
            return;
        }

        storeUserManager.registerToken()
                .doOnSuccess(snsToken -> Log.d(TAG, "onNewToken: snsToken=" + snsToken))
                .subscribe();
    }

}

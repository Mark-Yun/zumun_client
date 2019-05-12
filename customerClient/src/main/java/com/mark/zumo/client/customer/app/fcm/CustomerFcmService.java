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

package com.mark.zumo.client.customer.app.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mark.zumo.client.customer.model.CustomerSessionManager;

import java.util.Map;

/**
 * Created by mark on 18. 6. 8.
 */
public class CustomerFcmService extends FirebaseMessagingService {

    private static final String TAG = "CustomerFcmService";

    private CustomerMessageHandler customerMessageHandler;
    private CustomerSessionManager customerSessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        customerMessageHandler = CustomerMessageHandler.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
            customerMessageHandler.handleMessage(this, data);
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

        customerSessionManager.registerToken();
    }
}

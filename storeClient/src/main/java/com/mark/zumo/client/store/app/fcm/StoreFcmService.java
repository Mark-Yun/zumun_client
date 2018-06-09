/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.app.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.store.model.OrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 6. 8.
 */
public class StoreFcmService extends FirebaseMessagingService {
    private final static String TAG = "StoreFcmService";

    private final static String KEY_ORDER_UUID = "menu_order_uuid";
    private final static String KEY_KAKAO_ACCESS_TOKEN = "kakao_access_token";
    private final static String KEY_PG_TOKEN = "pg_token";

    private OrderManager orderManager;

    @Override
    public void onCreate() {
        super.onCreate();
        orderManager = OrderManager.INSTANCE;
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

            String orderUuid = data.get(KEY_ORDER_UUID);
            String kakaoAccessToken = data.get(KEY_KAKAO_ACCESS_TOKEN);
            String pgToken = data.get(KEY_PG_TOKEN);

            PaymentToken paymentToken = new PaymentToken(orderUuid, kakaoAccessToken, pgToken);

            orderManager.putRequestedOrderBucket(paymentToken);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}

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
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.customer.model.NotificationHandler;

import java.util.Map;

/**
 * Created by mark on 18. 6. 8.
 */
public class CustomerFcmService extends FirebaseMessagingService {
    private final static String TAG = "StoreFcmService";

    private NotificationHandler notificationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHandler = NotificationHandler.INSTANCE;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);

            String uuid = data.get(MenuOrder.Schema.uuid);
            String orderName = data.get(MenuOrder.Schema.orderName);
            String orderNumber = data.get(MenuOrder.Schema.orderNumber);
            long createdDate = Long.parseLong(data.get(MenuOrder.Schema.createdDate));
            String customerUuid = data.get(MenuOrder.Schema.customerUuid);
            int state = Integer.parseInt(data.get(MenuOrder.Schema.state));
            String storeUuid = data.get(MenuOrder.Schema.storeUuid);
            int totalPrice = Integer.parseInt(data.get(MenuOrder.Schema.totalPrice));
            int totalQuantity = Integer.parseInt(data.get(MenuOrder.Schema.totalQuantity));

            MenuOrder menuOrder = new MenuOrder(uuid, orderName, customerUuid, storeUuid, orderNumber, createdDate, totalQuantity, totalPrice, state);
            notificationHandler.requestOrderProgressNotification(menuOrder);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}

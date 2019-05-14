/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.app.fcm;

import android.util.Log;

import com.mark.zumo.client.core.appserver.request.sns.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.sns.message.SnsMessage;
import com.mark.zumo.client.store.model.StoreOrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 9. 16.
 */
enum StoreMessageHandler {
    INSTANCE;

    private static final String TAG = "StoreMessageHandler";

    private StoreOrderManager storeOrderManager;

    StoreMessageHandler() {
        storeOrderManager = StoreOrderManager.INSTANCE;
    }

    void handleMessage(Map<String, String> data) {
        SnsMessage snsMessage = MessageFactory.create(data);
        if (snsMessage == null) {
            Log.e(TAG, "handleMessage: message is NULL!");
            return;
        }

        if (SnsMessage.Type.EVENT_ORDER_UPDATED.name().equals(snsMessage.messageType)) {
            onOrderUpdated(snsMessage.data);
        }
    }

    private void onOrderUpdated(final String orderUuid) {
        Log.d(TAG, "onOrderUpdated: " + orderUuid);
        storeOrderManager.putRequestedOrderBucket(orderUuid);
    }
}

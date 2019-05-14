/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.app.fcm;

import android.util.Log;

import com.mark.zumo.client.core.appserver.request.sns.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.sns.message.SnsMessage;
import com.mark.zumo.client.customer.model.CustomerOrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 9. 21.
 */
public enum CustomerMessageHandler {
    INSTANCE;

    public static final String TAG = "CustomerMessageHandler";

    private CustomerOrderManager customerOrderManager;

    CustomerMessageHandler() {
        customerOrderManager = CustomerOrderManager.INSTANCE;
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
        customerOrderManager.getMenuOrderFromApi(orderUuid)
                .subscribe();
    }
}

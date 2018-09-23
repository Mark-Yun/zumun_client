/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.app.fcm;

import com.mark.zumo.client.core.appserver.request.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.message.OrderCreatedMessage;
import com.mark.zumo.client.core.appserver.request.message.SnsMessage;
import com.mark.zumo.client.store.model.OrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 9. 16.
 */
enum StoreMessageHandler {
    INSTANCE;

    private OrderManager orderManager;

    StoreMessageHandler() {
        orderManager = OrderManager.INSTANCE;
    }

    void handleMessage(Map<String, String> data) {
        SnsMessage snsMessage = MessageFactory.create(data);

        if (snsMessage instanceof OrderCreatedMessage) {
            onOrderCreated((OrderCreatedMessage) snsMessage);
        }
    }

    private void onOrderCreated(OrderCreatedMessage message) {
        orderManager.putRequestedOrderBucket(message.orderUuid);
    }
}

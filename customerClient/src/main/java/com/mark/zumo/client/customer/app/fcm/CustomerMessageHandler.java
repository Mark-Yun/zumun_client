/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.app.fcm;

import com.mark.zumo.client.core.appserver.request.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.message.OrderAcceptedMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCompleteMessage;
import com.mark.zumo.client.core.appserver.request.message.SnsMessage;
import com.mark.zumo.client.customer.model.NotificationHandler;
import com.mark.zumo.client.customer.model.OrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 9. 21.
 */
public enum CustomerMessageHandler {
    INSTANCE;

    private NotificationHandler notificationHandler;
    private OrderManager orderManager;

    CustomerMessageHandler() {
        notificationHandler = NotificationHandler.INSTANCE;
        orderManager = OrderManager.INSTANCE;
    }

    void handleMessage(Map<String, String> data) {
        SnsMessage snsMessage = MessageFactory.create(data);
        if (snsMessage == null) {
            return;
        }

        if (snsMessage instanceof OrderAcceptedMessage) {
            onOrderAccepted((OrderAcceptedMessage) snsMessage);
        } else if (snsMessage instanceof OrderCompleteMessage) {
            onOrderComplete((OrderCompleteMessage) snsMessage);
        }
    }

    private void onOrderAccepted(OrderAcceptedMessage message) {
        orderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(notificationHandler::requestOrderProgressNotification)
                .subscribe();
    }

    private void onOrderComplete(OrderCompleteMessage message) {
        orderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(notificationHandler::requestOrderProgressNotification)
                .subscribe();
    }
}

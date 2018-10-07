/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.app.fcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mark.zumo.client.core.appserver.request.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.message.OrderAcceptedMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCompleteMessage;
import com.mark.zumo.client.core.appserver.request.message.SnsMessage;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.model.NotificationHandler;
import com.mark.zumo.client.customer.model.OrderManager;

import java.util.Map;

/**
 * Created by mark on 18. 9. 21.
 */
public enum CustomerMessageHandler {
    INSTANCE;

    public static final String TAG = "CustomerMessageHandler";
    private NotificationHandler notificationHandler;
    private OrderManager orderManager;

    CustomerMessageHandler() {
        notificationHandler = NotificationHandler.INSTANCE;
        orderManager = OrderManager.INSTANCE;
    }

    void handleMessage(Context context, Map<String, String> data) {
        SnsMessage snsMessage = MessageFactory.create(data);
        if (snsMessage == null) {
            Log.e(TAG, "handleMessage: message is NULL!");
            return;
        }

        if (snsMessage instanceof OrderAcceptedMessage) {
            onOrderAccepted(context, (OrderAcceptedMessage) snsMessage);
            sendOrderUpdated();
        } else if (snsMessage instanceof OrderCompleteMessage) {
            onOrderComplete(context, (OrderCompleteMessage) snsMessage);
            sendOrderUpdated();
        }
    }

    private void sendOrderUpdated() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ContextHolder.getContext());
        localBroadcastManager.sendBroadcast(new Intent(OrderRepository.ACTION_ORDER_UPDATED));
    }

    private void onOrderAccepted(Context context, OrderAcceptedMessage message) {
        Log.d(TAG, "onOrderAccepted: " + message);
        orderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(menuOrder -> notificationHandler.requestOrderProgressNotification(context, menuOrder))
                .subscribe();
    }

    private void onOrderComplete(Context context, OrderCompleteMessage message) {
        Log.d(TAG, "onOrderComplete: " + message);
        orderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(menuOrder -> notificationHandler.requestOrderProgressNotification(context, menuOrder))
                .subscribe();
    }
}

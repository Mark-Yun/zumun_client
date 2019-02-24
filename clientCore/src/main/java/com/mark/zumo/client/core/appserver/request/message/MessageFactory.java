/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.message;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by mark on 18. 9. 16.
 */
public final class MessageFactory {

    public static SnsMessage create(Map<String, String> data) {
        String messageType = data.get(SnsMessageContract.MessageType.key);
        if (messageType == null || TextUtils.isEmpty(messageType)) {
            return null;
        }

        switch (messageType) {
            case SnsMessageContract.MessageType.orderCreated:
                String orderUuid = data.get(SnsMessageContract.Order.key);
                return new OrderCreatedMessage(orderUuid);

            case SnsMessageContract.MessageType.orderAccepted:
                orderUuid = data.get(SnsMessageContract.Order.key);
                return new OrderAcceptedMessage(orderUuid);

            case SnsMessageContract.MessageType.orderComplete:
                orderUuid = data.get(SnsMessageContract.Order.key);
                return new OrderCompleteMessage(orderUuid);

            default:
                throw new UnsupportedOperationException();
        }
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.message;

/**
 * Created by mark on 18. 9. 16.
 */
public class OrderCreatedMessage extends SnsMessage {
    public OrderCreatedMessage(final String orderUuid) {
        super(SnsMessageContract.MessageType.orderCreated, orderUuid);
    }
}

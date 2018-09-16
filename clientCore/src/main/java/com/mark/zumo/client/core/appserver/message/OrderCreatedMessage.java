/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 9. 16.
 */
public class OrderCreatedMessage extends SnsMessage {
    @SerializedName(SnsMessageContract.Order.key)
    public final String orderUuid;

    public OrderCreatedMessage(final String orderUuid) {
        super(SnsMessageContract.MessageType.orderCreated);
        this.orderUuid = orderUuid;
    }
}

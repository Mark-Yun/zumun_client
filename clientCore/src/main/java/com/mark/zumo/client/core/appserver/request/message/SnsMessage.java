/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.message;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 9. 16.
 */
public class SnsMessage {
    @SerializedName(SnsMessageContract.MessageType.key)
    public final String messageType;
    @SerializedName(SnsMessageContract.Order.key)
    public final String orderUuid;

    public SnsMessage(final String messageType, final String orderUuid) {
        this.messageType = messageType;
        this.orderUuid = orderUuid;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }
}

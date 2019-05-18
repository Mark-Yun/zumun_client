/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.sns.message;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 9. 16.
 */
public class SnsMessage {
    @SerializedName(Schema.EVENT)
    public final String messageType;
    @SerializedName(Schema.DATA)
    public final String data;

    public SnsMessage(final String messageType, final String data) {
        this.messageType = messageType;
        this.data = data;
    }

    public enum Type {
        EVENT_ORDER_UPDATED
    }

    public static SnsMessage createOrderUpdated(final String orderUuid) {
        return new SnsMessage(Type.EVENT_ORDER_UPDATED.name(), orderUuid);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    /**
     * Created by mark on 18. 9. 16.
     */

    public interface Schema {
        String EVENT = "event";
        String DATA = "data";
    }
}

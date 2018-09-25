/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.request;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.appserver.request.message.SnsMessage;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 9. 21.
 */
public class SnsSendMessageRequest {

    @SerializedName(Schema.UUID)
    public final String uuid;
    @SerializedName(Schema.MESSAGE)
    public final SnsMessage message;

    public SnsSendMessageRequest(final String uuid, final SnsMessage message) {
        this.uuid = uuid;
        this.message = message;
    }

    interface Schema {
        String UUID = "uuid";
        String MESSAGE = "message";
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.sns.message;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by mark on 18. 9. 16.
 */
public final class MessageFactory {

    private static final String TAG = "MessageFactory";

    private MessageFactory() {
    }

    public static SnsMessage create(Map<String, String> data) {
        String messageTypeString = data.get(SnsMessage.Schema.EVENT);
        if (messageTypeString == null || TextUtils.isEmpty(messageTypeString)) {
            return null;
        }
        String dataString = data.get(SnsMessage.Schema.DATA);

        return new SnsMessage(messageTypeString, dataString);
    }
}

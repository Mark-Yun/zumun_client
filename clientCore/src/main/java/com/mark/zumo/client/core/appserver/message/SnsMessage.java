/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.message;

/**
 * Created by mark on 18. 9. 16.
 */
public abstract class SnsMessage {
    public final String messageType;

    public SnsMessage(final String messageType) {
        this.messageType = messageType;
    }
}

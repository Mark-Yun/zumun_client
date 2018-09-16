/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.message;

/**
 * Created by mark on 18. 9. 16.
 */
public interface SnsMessageContract {

    interface MessageType {
        String key = "message_type";
        String orderCreated = "order_created";
        String orderAccepted = "order_accepted";
        String orderComplete = "order_complete";
    }

    interface Order {
        String key = "order_uuid";
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.payment;

/**
 * Created by mark on 18. 5. 21.
 */
public interface PaymentContract {
    interface ReadyStatus {
        int NOT_SUPPORT = 0;
        int NOT_READY = 1;
        int READY = 2;
        int ERROR = -1;
    }

}

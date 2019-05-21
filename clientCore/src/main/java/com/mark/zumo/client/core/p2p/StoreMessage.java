/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p;

import com.google.android.gms.nearby.messages.Message;
import com.mark.zumo.client.core.database.entity.Store;

/**
 * Created by mark on 18. 4. 30.
 */

class StoreMessage {

    private static final String SPLIT = ",";
    public final Store store;

    StoreMessage(Store store) {
        this.store = store;
    }

    Message toMessage() {
        String rawMessage = store.uuid + SPLIT
                + store.name + SPLIT
                + store.latitude + SPLIT
                + store.longitude + SPLIT;

        return new Message(rawMessage.getBytes());
    }
}

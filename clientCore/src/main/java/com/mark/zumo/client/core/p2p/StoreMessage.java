/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p;

import com.google.android.gms.nearby.messages.Message;
import com.mark.zumo.client.core.entity.Store;

/**
 * Created by mark on 18. 4. 30.
 */

class StoreMessage {

    private static final String SPLIT = ",";
    public final Store store;

    StoreMessage(Store store) {
        this.store = store;
    }

    static StoreMessage from(Message message) {
        byte[] content = message.getContent();
        String rawMessage = new String(content);
        String[] splitContent = rawMessage.split(SPLIT);

        String id = splitContent[0];
        String name = splitContent[1];
        long latitude = Long.parseLong(splitContent[2]);
        long longitude = Long.parseLong(splitContent[3]);
        String coverImageUrl = splitContent[4];
        String thumbnailImageUrl = splitContent[5];
        String fcmToken = splitContent[6];

        Store store = new Store(id, name, latitude, longitude, coverImageUrl, thumbnailImageUrl, fcmToken);
        return new StoreMessage(store);
    }

    Message toMessage() {
        String rawMessage = store.uuid + SPLIT
                + store.name + SPLIT
                + store.latitude + SPLIT
                + store.longitude + SPLIT;

        return new Message(rawMessage.getBytes());
    }
}

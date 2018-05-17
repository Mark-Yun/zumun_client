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

        Store store = new Store(id, name, latitude, longitude);
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

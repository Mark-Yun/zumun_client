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

        long id = Long.parseLong(splitContent[0]);
        String name = splitContent[1];
        long storeOwnerId = Long.parseLong(splitContent[2]);
        long createdDate = Long.parseLong(splitContent[3]);
        long latitude = Long.parseLong(splitContent[4]);
        long longitude = Long.parseLong(splitContent[5]);

        Store store = new Store(id, name, storeOwnerId, createdDate, latitude, longitude);
        return new StoreMessage(store);
    }

    Message toMessage() {
        String rawMessage = store.id + SPLIT
                + store.name + SPLIT
                + store.storeOwnerId + SPLIT
                + store.createdDate + SPLIT
                + store.latitude + SPLIT
                + store.longitude + SPLIT;

        return new Message(rawMessage.getBytes());
    }
}

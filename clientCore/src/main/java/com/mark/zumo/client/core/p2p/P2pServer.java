package com.mark.zumo.client.core.p2p;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.mark.zumo.client.core.entity.Store;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pServer {

    private static final String TAG = "P2pServer";
    private Activity activity;
    private Store store;
    private Message storeMessage;

    public P2pServer(Activity activity, Store store) {
        this.activity = activity;
        this.store = store;
    }

    public void publish() {
        Log.d(TAG, "publish");
        storeMessage = new StoreMessage(store).toMessage();
        messageClient().publish(storeMessage);
    }

    private MessagesClient messageClient() {
        return Nearby.getMessagesClient(activity);
    }

    public void unpublish() {
        Log.d(TAG, "unpublish");
        if (storeMessage == null) return;

        messageClient().unpublish(storeMessage);
        storeMessage = null;
    }
}

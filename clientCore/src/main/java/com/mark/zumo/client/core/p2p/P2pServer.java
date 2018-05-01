package com.mark.zumo.client.core.p2p;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.mark.zumo.client.core.entity.Store;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pServer {

    private static final String TAG = "P2pServer";

    private final String SERVICE_ID;

    private Activity activity;
    private Store store;
    private Message storeMessage;

    public P2pServer(Activity activity, Store store) {
        this.activity = activity;
        this.store = store;
        SERVICE_ID = store.name; //TODO: Auth
    }

    @NonNull
    private static ConnectionLifecycleCallback connectionLifecycleCallback() {
        return new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                // Automatically accept the connection on both sides.
                //Nearby.getConnectionsClient(context).acceptConnection(endpointId, mPayloadCallback);
                //TODO: Auth
            }

            @Override
            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        break;
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.

            }
        };
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

    private void onSuccess(Void unusedResult) {
        Log.d(TAG, "onSuccess:");
    }

    private void onFailure(@NonNull Exception e) {
        Log.e(TAG, "onFailure: ", e);
    }

    public void startAdvertising() {
        ConnectionLifecycleCallback connectionLifecycleCallback = connectionLifecycleCallback();

        connectionsClient()
                .startAdvertising(store.name, SERVICE_ID, connectionLifecycleCallback, Options.ADVERTISING)
                .addOnSuccessListener(this::onSuccess)
                .addOnFailureListener(this::onFailure);
    }

    public void stopAdvertising() {
        connectionsClient().stopAdvertising();
    }

    private ConnectionsClient connectionsClient() {
        return Nearby.getConnectionsClient(activity);
    }

    private PayloadCallback payloadCallback() {
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {

            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String payload, @NonNull PayloadTransferUpdate update) {

            }
        };
    }
}

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
import com.mark.zumo.client.core.entity.user.CustomerUser;
import com.mark.zumo.client.core.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pServer {

    private static final String TAG = "P2pServer";

    private Activity activity;
    private Store store;
    private Message storeMessage;

    private UserRepository userRepository;

    public P2pServer(Activity activity, Store store) {
        this.activity = activity;
        this.store = store;

        userRepository = UserRepository.from(activity);
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

    private void onSuccessAdvertising(Void unusedResult) {
        Log.d(TAG, "onSuccessAdvertising:");
    }

    private void onFailureAdvertising(@NonNull Exception e) {
        Log.e(TAG, "onFailureAdvertising: ", e);
    }

    public Observable<CustomerUser> startAdvertising() {
        return Observable.create(e -> {
            connectionsClient().startAdvertising(store.name, Options.SERVICE_ID,
                    new ConnectionLifecycleCallback() {
                        @Override
                        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                            // Automatically accept the connection on both sides.
                            Log.d(TAG, "onConnectionInitiated: endpointID=" + endpointId
                                    + "ConnectionInfo["
                                    + " authenticationToken=" + connectionInfo.getAuthenticationToken()
                                    + " endpointName=" + connectionInfo.getEndpointName()
                                    + "]");

                            long userId = Long.parseLong(connectionInfo.getEndpointName());
                            userRepository.findCustomerUserById(userId).subscribe(e::onNext);

                            //TODO Auth customer
                            connectionsClient().acceptConnection(endpointId,
                                    new PayloadCallback() {
                                        @Override
                                        public void onPayloadReceived(@NonNull String endpointId1, @NonNull Payload payload) {
                                            Log.d(TAG, "onPayloadReceived: endpointId=" + endpointId1
                                                    + " Payload["
                                                    + " id=" + payload.getId()
                                                    + " type=" + payload.getType()
                                                    + " length=" + payload.asBytes().length
                                                    + "]");
                                        }

                                        @Override
                                        public void onPayloadTransferUpdate(@NonNull String payload, @NonNull PayloadTransferUpdate update) {

                                            Log.d(TAG, "onPayloadTransferUpdate: payload=" + payload
                                                    + " PayloadTransferUpdate["
                                                    + " payloadId=" + update.getPayloadId()
                                                    + " bytesTransferred=" + update.getBytesTransferred()
                                                    + " status=" + update.getStatus()
                                                    + " totalBytes=" + update.getTotalBytes()
                                                    + "]");
                                        }
                                    })
                                    .addOnSuccessListener(this::onSuccessAcceptConnection)
                                    .addOnFailureListener(this::onFailureAcceptConnection);
                        }

                        private void onSuccessAcceptConnection(Void unusedResult) {
                            Log.d(TAG, "onSuccessAcceptConnection: ");
                        }

                        private void onFailureAcceptConnection(Exception e) {
                            Log.e(TAG, "onFailureAcceptConnection: ", e);
                        }

                        @Override
                        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result1) {
                            int statusCode = result1.getStatus().getStatusCode();
                            switch (statusCode) {
                                case ConnectionsStatusCodes.STATUS_OK:
                                    // We're connected! Can now start sending and receiving data.
                                    Log.d(TAG, "onConnectionResult: STATUS_OK");
                                    //TODO: Send File
                                    break;
                                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                    // The connection was rejected by one or both sides.
                                    Log.d(TAG, "onConnectionResult: STATUS_CONNECTION_REJECTED");
                                    break;
                                case ConnectionsStatusCodes.STATUS_ERROR:
                                    // The connection broke before it was able to be accepted.
                                    Log.d(TAG, "onConnectionResult: STATUS_ERROR");
                                    break;
                            }

                            Log.d(TAG, "onConnectionResult: endpointId=" + endpointId
                                    + "ConnectionResolution["
                                    + " status=" + result1.getStatus()
                                    + "]");
                        }

                        @Override
                        public void onDisconnected(@NonNull String endpointId) {
                            // We've been disconnected from this endpoint. No more data can be
                            // sent or received.
                            Log.d(TAG, "onDisconnected: endpointId=" + endpointId);
                        }
                    }, Options.ADVERTISING)
                    .addOnSuccessListener(this::onSuccessAdvertising)
                    .addOnFailureListener(this::onFailureAdvertising);
        });
    }

    public void stopAdvertising() {
        connectionsClient().stopAdvertising();
    }

    private ConnectionsClient connectionsClient() {
        return Nearby.getConnectionsClient(activity);
    }

}

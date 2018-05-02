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
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.CustomerUser;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pClient {

    private static final String TAG = "P2pClient";
    private static final long TIME_OUT = 5000;

    private Activity activity;
    private CustomerUser customerUser;

    private SetObservable<Store> observable;
    private MessageListener messageListener;

    public P2pClient(Activity activity, CustomerUser currentUser) {
        this.activity = activity;
        this.customerUser = currentUser;
        messageListener = messageListener();
    }

    private MessagesClient messageClient() {
        return Nearby.getMessagesClient(activity);
    }

    public Observable<Set<Store>> subscribe() {
        return Observable.create(emitter -> {
            observable = new SetObservable<>();
            observable.addObserver((o, arg) -> emitter.onNext(observable.set));
            messageClient().subscribe(messageListener);
        });
    }

    @NonNull
    private MessageListener messageListener() {
        return new MessageListener() {
            @Override
            public void onBleSignalChanged(Message message, BleSignal bleSignal) {
                Store store = StoreMessage.from(message).store;
                Log.d(TAG, "onBleSignalChanged: " + store + NearbyUtil.convertString(bleSignal));
            }

            @Override
            public void onDistanceChanged(Message message, Distance distance) {
                Store store = StoreMessage.from(message).store;
                Log.d(TAG, "onDistanceChanged: " + store + NearbyUtil.convertString(distance));
            }

            @Override
            public void onFound(Message message) {
                Store store = StoreMessage.from(message).store;
                Log.d(TAG, "onFound: " + store);

                observable.add(store);
            }

            @Override
            public void onLost(Message message) {
                Store store = StoreMessage.from(message).store;
                Log.d(TAG, "onLost: " + store);

                observable.remove(store);
            }
        };
    }

    public void unsubscribe() {
        messageClient().unsubscribe(messageListener);
        clear();
    }

    public Observable<List<MenuItem>> loadMenuItemList(Store store) {
        return Observable.create(e -> {

        });
    }

    private void clear() {
        observable.set.clear();
        observable.deleteObservers();
    }

    private void requestConnection(Store store) {
    }

    private ConnectionsClient connectionsClient() {
        return Nearby.getConnectionsClient(activity);
    }

    public void startDiscovery(Store store) {
        connectionsClient().startDiscovery(store.name, endpointDiscoveryCallback(), Options.DISCOVERY)
                .addOnSuccessListener(this::onDiscoverySuccess)
                .addOnFailureListener(this::onDiscoveryFailure);
    }

    public void stopDiscovery() {
        connectionsClient().stopDiscovery();
    }

    private void requestConnection(String endpointId) {
        connectionsClient().requestConnection(customerUser.name, endpointId, connectionLifecycleCallback())
                .addOnSuccessListener(this::onRequestConnectionSuccess)
                .addOnFailureListener(this::onRequestConnectionFailure);
    }

    @NonNull
    private ConnectionLifecycleCallback connectionLifecycleCallback() {
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

    @NonNull
    private EndpointDiscoveryCallback endpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {

            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {

            }
        };
    }

    private void onDiscoverySuccess(Void unusedResult) {
        Log.d(TAG, "onDiscoverySuccess:");
    }

    private void onDiscoveryFailure(@NonNull Exception e) {
        Log.e(TAG, "onDiscoveryFailure: ", e);
    }

    private void onRequestConnectionSuccess(Void unusedResult) {
        Log.d(TAG, "onRequestConnectionSuccess:");
    }

    private void onRequestConnectionFailure(@NonNull Exception e) {
        Log.e(TAG, "onRequestConnectionFailure: ", e);
    }
}

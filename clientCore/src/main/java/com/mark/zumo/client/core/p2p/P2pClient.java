package com.mark.zumo.client.core.p2p;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
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
import com.mark.zumo.client.core.dao.Converters;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.p2p.observable.SetObservable;
import com.mark.zumo.client.core.p2p.packet.Packet;
import com.mark.zumo.client.core.p2p.packet.Request;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pClient {

    private static final String TAG = "P2pClient";

    private Activity activity;
    private GuestUser guestUser;

    private SetObservable<Store> storeObservable;
    private MessageListener messageListener;

    private String currentEndpointId;

    private SimpleArrayMap<UUID, String> endPointMap = new SimpleArrayMap<>();
    private SimpleArrayMap<String, Payload> incomingPayloads = new SimpleArrayMap<>();

    public P2pClient(Activity activity, GuestUser currentUser) {
        this.activity = activity;
        this.guestUser = currentUser;
        messageListener = messageListener();
    }

    private MessagesClient messageClient() {
        return Nearby.getMessagesClient(activity);
    }

    public Observable<Set<Store>> subscribe() {
        Log.d(TAG, "subscribe: ");
        return Observable.create(emitter -> {
            storeObservable = new SetObservable<>();
            storeObservable.addObserver((o, arg) -> emitter.onNext(storeObservable.set));
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

                storeObservable.add(store);
            }

            @Override
            public void onLost(Message message) {
                Store store = StoreMessage.from(message).store;
                Log.d(TAG, "onLost: " + store);

                storeObservable.remove(store);
            }
        };
    }

    public void unsubscribe() {
        Log.d(TAG, "unsubscribe: ");
        messageClient().unsubscribe(messageListener);
        clearOnUnsubscribe();
    }

//    public Observable<List<Menu>> loadMenuItemList(Store store) {
//        return Observable.create(e -> {
//
//        });
//    }

    private void clearOnUnsubscribe() {
        storeObservable.set.clear();
        storeObservable.deleteObservers();
    }

    private ConnectionsClient connectionsClient() {
        return Nearby.getConnectionsClient(activity);
    }

    private Single<String> startDiscovery() {
        Log.d(TAG, "startDiscovery: ");
        return Single.create(e ->
                connectionsClient().startDiscovery(Options.SERVICE_ID, new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
                        Log.d(TAG, "onEndpointFound: endpointId=" + endpointId
                                + " DiscoveredEndpointInfo["
                                + "endpointName=" + endpointInfo.getEndpointName()
                                + " serviceId=" + endpointInfo.getServiceId()
                                + "]");

                        //TODO Auth Store by endpointInfo
                        e.onSuccess(endpointId);
                    }

                    @Override
                    public void onEndpointLost(@NonNull String endpointId) {
                        Log.d(TAG, "onEndpointLost: endpointId=" + endpointId);
                    }
                }, Options.DISCOVERY)
                        .addOnSuccessListener(this::onDiscoverySuccess)
                        .addOnFailureListener(this::onDiscoveryFailure));
    }

    public void stopDiscovery() {
        Log.d(TAG, "stopDiscovery: ");
        connectionsClient().stopDiscovery();
    }

    private Single<String> requestConnection(String endPointId, Packet packet) {
        Log.d(TAG, "requestConnection: " + endPointId);
        return Single.create(e -> connectionsClient().requestConnection(String.valueOf(guestUser.getUuid()), endPointId,
                new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId1, @NonNull ConnectionInfo connectionInfo) {
                        // Automatically accept the connection on both sides.
                        Log.d(TAG, "onConnectionInitiated: endpointID=" + endpointId1
                                + " ConnectionInfo["
                                + " authenticationToken=" + connectionInfo.getAuthenticationToken()
                                + " endpointName=" + connectionInfo.getEndpointName()
                                + "]");
                        //TODO: Auth
                        saveConnectionInfo(endpointId1, connectionInfo);
                        e.onSuccess(endpointId1);
                    }

                    @Override
                    public void onConnectionResult(@NonNull String endpointId1, @NonNull ConnectionResolution result1) {
                        int statusCode = result1.getStatus().getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                // We're connected! Can now start sending and receiving data.
                                Log.d(TAG, "onConnectionResult: STATUS_OK");
                                currentEndpointId = endpointId1;

                                Single.just(packet)
                                        .flatMap(packet -> sendPayload(endpointId1, packet))
                                        .subscribeOn(Schedulers.from(Executors.newScheduledThreadPool(5)))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(payload -> e.onSuccess("Send Payload Success" + payload));
                                break;
                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                // The connection was rejected by one or both sides.
                                Log.d(TAG, "onConnectionResult: STATUS_CONNECTION_REJECTED");
                                e.onError(new RuntimeException("TODO: STATUS_CONNECTION_REJECTED"));
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                // The connection broke before it was able to be accepted.
                                Log.d(TAG, "onConnectionResult: STATUS_ERROR");
                                e.onError(new RuntimeException("TODO: STATUS_ERROR"));
                                break;
                        }

                        Log.d(TAG, "onConnectionResult: endpointId=" + endpointId1
                                + "ConnectionResolution["
                                + " status=" + result1.getStatus()
                                + "]");
                    }

                    @Override
                    public void onDisconnected(@NonNull String endpointId1) {
                        // We've been disconnected payloadFrom this endpoint. No more data can be
                        // sent or received.
                        Log.d(TAG, "onDisconnected: endpointId=" + endpointId1);
                    }
                })
                .addOnSuccessListener(this::onRequestConnectionSuccess)
                .addOnFailureListener(this::onRequestConnectionFailure));
    }

    private Single<Payload> sendPayload(String endpointId, Packet<MenuOrder> packet) {
        return Single.create(e -> {
            Payload payload = NearbyUtil.payloadFrom(packet);
            connectionsClient().sendPayload(endpointId, payload)
                    .addOnSuccessListener(aVoid -> onSuccessSendPayload(e, endpointId, payload))
                    .addOnFailureListener(Runnable::run, this::onFailureSendPayload);
        });
    }

    private void onSuccessSendPayload(SingleEmitter<Payload> emitter, String endpointId, Payload payload) {
        Log.d(TAG, "onSuccessSendPayload: endpointId=" + endpointId);
        emitter.onSuccess(payload);
    }

    private void onFailureSendPayload(Exception e) {
        Log.e(TAG, "onFailureSendPayload: ", e);
    }

    private void saveConnectionInfo(@NonNull String endpointId1, @NonNull ConnectionInfo connectionInfo) {
        String storeId = connectionInfo.getEndpointName();
        UUID uuid = Converters.fromBinary(storeId.getBytes());
        endPointMap.put(uuid, endpointId1);
    }

    private Single<Payload> acceptConnection(String endpointId) {
        Log.d(TAG, "acceptConnection: endpointId=" + endpointId);
        return Single.create(e ->
                connectionsClient().acceptConnection(endpointId, new PayloadCallback() {
                    @Override
                    public void onPayloadReceived(@NonNull String endpointId1, @NonNull Payload payload) {
                        Log.d(TAG, "onPayloadReceived: endpointId=" + endpointId1
                                + " Payload["
                                + " uuid=" + payload.getId()
                                + " type=" + payload.getType()
                                + "]");
                        //TODO: Auth Endpoint1

                        switch (payload.getType()) {
                            case Payload.Type.BYTES:
                                e.onSuccess(payload);

                            case Payload.Type.STREAM:
                                incomingPayloads.put(endpointId1, payload);
                                break;
                            case Payload.Type.FILE:
                                incomingPayloads.put(endpointId1, payload);
                                break;
                        }
                    }

                    @Override
                    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {

                        Log.d(TAG, "onPayloadTransferUpdate:" + endpointId
                                + " PayloadTransferUpdate["
                                + "payloadId=" + update.getPayloadId()
                                + " bytesTransferred=" + update.getBytesTransferred()
                                + " status=" + update.getStatus()
                                + " totalBytes=" + update.getTotalBytes()
                                + "]");

                        if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                            Payload payload = incomingPayloads.get(endpointId);
                            if (payload != null && payload.getType() != Payload.Type.BYTES) {
                                e.onSuccess(payload);
                            }
                        }
                    }
                })
                        .addOnSuccessListener(this::onSuccessAcceptConnection)
                        .addOnFailureListener(this::onFailureAcceptConnection));
    }

    @SuppressWarnings("unused")
    private void onSuccessAcceptConnection(Void unusedResult) {
        Log.d(TAG, "onSuccessAcceptConnection: ");
        stopDiscovery();
    }

    private void onFailureAcceptConnection(@NonNull Exception e) {
        Log.e(TAG, "onFailureAcceptConnection: ", e);
    }

    private Single<List<Menu>> convertMenuItemFromPayload(final Payload payload) {
        return Single.create(e -> {
            byte[] bytes = NearbyUtil.bytesFrom(payload);
            Packet<List<Menu>> menuItemsPacket = new Packet<>(bytes);
            e.onSuccess(menuItemsPacket.get());
        });
    }

    public Single<List<Menu>> acquireMenuItems() {
        Log.d(TAG, "acquireMenuItems:");
        Packet<Request> packet = new Packet<>(Request.REQ_MENU_ITEM_LIST);

        return startDiscovery()
                .flatMap(endpointId -> requestConnection(endpointId, packet))
                .flatMap(this::acceptConnection)
                .flatMap(this::convertMenuItemFromPayload)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(menuItemList -> connectionsClient().disconnectFromEndpoint(currentEndpointId))
                .doOnSuccess(unUsedResult -> currentEndpointId = null);
    }

    public Single<String> sendOrder(MenuOrder menuOrder, UUID storeUuid) {
        String endpointId = endPointMap.get(storeUuid);
        Packet<MenuOrder> packet = new Packet<>(menuOrder);
        return requestConnection(endpointId, packet)
                .flatMap(this::acceptConnection)
                .doOnSuccess(menuItemList -> connectionsClient().disconnectFromEndpoint(currentEndpointId))
                .doOnSuccess(unUsedResult -> currentEndpointId = null)
                .map(Payload::getId)
                .map(String::valueOf);
    }

    @SuppressWarnings("unused")
    private void onDiscoverySuccess(Void unusedResult) {
        Log.d(TAG, "onDiscoverySuccess:");
    }

    private void onDiscoveryFailure(@NonNull Exception e) {
        Log.e(TAG, "onDiscoveryFailure: ", e);
    }

    @SuppressWarnings("unused")
    private void onRequestConnectionSuccess(Void unusedResult) {
        Log.d(TAG, "onRequestConnectionSuccess:");
    }

    private void onRequestConnectionFailure(@NonNull Exception e) {
        Log.e(TAG, "onRequestConnectionFailure: ", e);
    }
}

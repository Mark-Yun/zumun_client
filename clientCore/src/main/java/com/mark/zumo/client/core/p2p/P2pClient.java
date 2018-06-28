/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

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
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.observable.SetObservable;
import com.mark.zumo.client.core.p2p.packet.Packet;
import com.mark.zumo.client.core.p2p.packet.Request;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum P2pClient {
    INSTANCE;

    private static final String TAG = "P2pClient";

    private SetObservable<Store> storeObservable;
    private MessageListener messageListener;

    private String currentEndpointId;

    private final SimpleArrayMap<String, String> endPointMap;
    private final SimpleArrayMap<String, Payload> incomingPayloads;

    private MessagesClient messagesClient;
    private ConnectionsClient connectionsClient;
    private String sessionId;

    P2pClient() {
        endPointMap = new SimpleArrayMap<>();
        incomingPayloads = new SimpleArrayMap<>();
    }

    private MessagesClient messageClient(Activity activity) {
        return Nearby.getMessagesClient(activity);
    }

    public Observable<Set<Store>> subscribe(Activity activity) {
        Log.d(TAG, "subscribe: ");
        return Observable.create((ObservableOnSubscribe<Set<Store>>) emitter -> {
            storeObservable = new SetObservable<>();
            storeObservable.addObserver((o, arg) -> emitter.onNext(storeObservable.set));
            messagesClient = messageClient(activity);
            messagesClient.subscribe(messageListener);
        }).subscribeOn(Schedulers.io());
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
        messagesClient.unsubscribe(messageListener);
        clearOnUnsubscribe();
    }

    private void clearOnUnsubscribe() {
        storeObservable.set.clear();
        storeObservable.deleteObservers();
    }

    private ConnectionsClient connectionsClient(Activity activity) {
        return Nearby.getConnectionsClient(activity);
    }

    private void saveEndpointInfo(String endpointId, DiscoveredEndpointInfo endpointInfo) {
        endPointMap.put(endpointInfo.getEndpointName(), endpointId);
        endPointMap.put(endpointId, endpointInfo.getEndpointName());
    }

    private Maybe<String> startDiscovery(Activity activity, String sessionId) {
        Log.d(TAG, "startDiscovery: ");
        return Maybe.create(e -> {
            this.sessionId = sessionId;
            connectionsClient = connectionsClient(activity);
            connectionsClient.startDiscovery(Options.SERVICE_ID, new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
                    Log.d(TAG, "onEndpointFound: endpointId=" + endpointId
                            + " DiscoveredEndpointInfo["
                            + "endpointName=" + endpointInfo.getEndpointName()
                            + " serviceId=" + endpointInfo.getServiceId()
                            + "]");

                    saveEndpointInfo(endpointId, endpointInfo);
                    e.onSuccess(endpointId);
                }

                @Override
                public void onEndpointLost(@NonNull String endpointId) {
                    Log.d(TAG, "onEndpointLost: endpointId=" + endpointId);
                }
            }, Options.DISCOVERY)
                    .addOnSuccessListener(this::onDiscoverySuccess)
                    .addOnFailureListener(this::onDiscoveryFailure);
        });
    }

    public void stopDiscovery() {
        Log.d(TAG, "stopDiscovery: ");
        connectionsClient.stopDiscovery();
    }

    public void clear() {
        endPointMap.clear();
        incomingPayloads.clear();

        if (messagesClient != null) {
            messagesClient.unsubscribe(messageListener);
            messagesClient = null;
        }
        if (connectionsClient != null) {
            connectionsClient.stopDiscovery();
            connectionsClient.stopAllEndpoints();
            connectionsClient = null;
        }

        sessionId = "";
    }

    private Maybe<String> requestConnection(String endPointId, Packet packet) {
        Log.d(TAG, "requestConnection: " + endPointId);
        return Maybe.create(e -> connectionsClient.requestConnection(sessionId, endPointId,
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
//                        saveConnectionInfo(endpointId1, connectionInfo);
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
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnSuccess(payload -> e.onSuccess("Send Payload Success" + payload)).subscribe();
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
        return Single.create((SingleOnSubscribe<Payload>) e -> {
            Payload payload = NearbyUtil.payloadFrom(packet);
            connectionsClient.sendPayload(endpointId, payload)
                    .addOnSuccessListener(aVoid -> onSuccessSendPayload(e, endpointId, payload))
                    .addOnFailureListener(Runnable::run, this::onFailureSendPayload);
        }).subscribeOn(Schedulers.from(Executors.newScheduledThreadPool(5)));
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
        endPointMap.put(storeId, endpointId1);
    }

    private Maybe<Payload> acceptConnection(String endpointId) {
        Log.d(TAG, "acceptConnection: endpointId=" + endpointId);
        return Maybe.create(e ->
                connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                    @Override
                    public void onPayloadReceived(@NonNull String endpointId1, @NonNull Payload payload) {
                        Log.d(TAG, "onPayloadReceived: endpointId=" + endpointId1
                                + " Payload["
                                + " menu_uuid=" + payload.getId()
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

    private Maybe<List<Menu>> convertMenuItemFromPayload(final Payload payload) {
        return Maybe.create(e -> {
            byte[] bytes = NearbyUtil.bytesFrom(payload);
            Packet<List<Menu>> menuItemsPacket = new Packet<>(bytes);
            e.onSuccess(menuItemsPacket.get());
        });
    }

    public Maybe<List<Menu>> acquireMenuItems(Activity activity, String sessionId) {
        Log.d(TAG, "acquireMenuItems:");

        return startDiscovery(activity, sessionId)
                .flatMap(endpointId -> requestConnection(endpointId, new Packet<>(Request.REQ_MENU_ITEM_LIST)))
                .flatMap(this::acceptConnection)
                .flatMap(this::convertMenuItemFromPayload)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(menuItemList -> connectionsClient.disconnectFromEndpoint(currentEndpointId))
                .doOnSuccess(unUsedResult -> currentEndpointId = null)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> requestMenuItems(String storeId) {
        return Maybe.just(storeId)
                .map(endPointMap::get)
                .doOnError(throwable -> Log.e(TAG, "requestMenuItems: ", throwable))
                .flatMap(endpointId -> requestConnection(endpointId, new Packet<>(Request.REQ_MENU_ITEM_LIST)))
                .flatMap(this::acceptConnection)
                .flatMap(this::convertMenuItemFromPayload)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(menuItemList -> connectionsClient.disconnectFromEndpoint(currentEndpointId))
                .doOnSuccess(unUsedResult -> currentEndpointId = null)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> findStore(Activity activity, String sessionId) {
        return startDiscovery(activity, sessionId)
                .map(endPointMap::get)
                .doOnError(throwable -> Log.e(TAG, "findStore: ", throwable))
                .subscribeOn(Schedulers.newThread());
    }

    public Maybe<String> sendOrder(MenuOrder menuOrder, String storeUuid) {
        String endpointId = endPointMap.get(storeUuid);
        Packet<MenuOrder> packet = new Packet<>(menuOrder);
        return requestConnection(endpointId, packet)
                .flatMap(this::acceptConnection)
                .doOnSuccess(menuItemList -> connectionsClient.disconnectFromEndpoint(currentEndpointId))
                .doOnSuccess(unUsedResult -> currentEndpointId = null)
                .map(Payload::getId)
                .map(String::valueOf)
                .subscribeOn(Schedulers.io());
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

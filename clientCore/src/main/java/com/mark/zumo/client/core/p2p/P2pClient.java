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
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum P2pClient {
    INSTANCE;

    private static final String TAG = "P2pClient";

    private final SimpleArrayMap<String, String> endPointMap;
    private final SimpleArrayMap<String, Payload> incomingPayloads;

    private ConnectionsClient connectionsClient;
    private String sessionId;

    P2pClient() {
        endPointMap = new SimpleArrayMap<>();
        incomingPayloads = new SimpleArrayMap<>();
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

        if (connectionsClient != null) {
            connectionsClient.stopDiscovery();
            connectionsClient.stopAllEndpoints();
            connectionsClient = null;
        }

        sessionId = "";
    }

    private void saveConnectionInfo(@NonNull String endpointId1, @NonNull ConnectionInfo connectionInfo) {
        String storeId = connectionInfo.getEndpointName();
        endPointMap.put(storeId, endpointId1);
    }

    @SuppressWarnings("unused")
    private void onSuccessAcceptConnection(Void unusedResult) {
        Log.d(TAG, "onSuccessAcceptConnection: ");
        stopDiscovery();
    }

    public Maybe<String> findStore(Activity activity, String customerUuid) {
        return startDiscovery(activity, customerUuid)
                .map(endPointMap::get)
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
}

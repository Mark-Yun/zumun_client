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
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.packet.CombinedResult;
import com.mark.zumo.client.core.p2p.packet.Packet;
import com.mark.zumo.client.core.p2p.packet.PacketType;
import com.mark.zumo.client.core.p2p.packet.Request;
import com.mark.zumo.client.core.p2p.packet.Response;
import com.mark.zumo.client.core.repository.MenuItemRepository;

import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;

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

    @SuppressWarnings("unused")
    private void onSuccessAdvertising(Void unusedResult) {
        Log.d(TAG, "onSuccessAdvertising:");
    }

    private void onFailureAdvertising(@NonNull Exception e) {
        Log.e(TAG, "onFailureAdvertising: ", e);
    }

    public Observable<String> findCustomer(Store store) {
        return Observable.just(store)
                .map(storeConsumer -> storeConsumer.id)
                .map(String::valueOf)
                .flatMap(this::startAdvertising)
                .flatMap(this::acceptConnection)
                .flatMap(this::processPayload)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(4)));
    }

    private Observable<String> startAdvertising(String nickName) {
        return Observable.create(e ->
                connectionsClient().startAdvertising(nickName, Options.SERVICE_ID,
                        new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                                // Automatically accept the connection on both sides.
                                Log.d(TAG, "onConnectionInitiated: endpointID=" + endpointId
                                        + "ConnectionInfo["
                                        + " authenticationToken=" + connectionInfo.getAuthenticationToken()
                                        + " endpointName=" + connectionInfo.getEndpointName()
                                        + "]");

                                //TODO Auth customer
                                e.onNext(endpointId);
                            }

                            @Override
                            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result1) {
                                int statusCode = result1.getStatus().getStatusCode();
                                switch (statusCode) {
                                    case ConnectionsStatusCodes.STATUS_OK:
                                        // We're connected! Can now start sending and receiving data.
                                        Log.d(TAG, "onConnectionResult: STATUS_OK");
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
                                        + " ConnectionResolution["
                                        + " status=" + result1.getStatus()
                                        + "]");
                            }

                            @Override
                            public void onDisconnected(@NonNull String endpointId) {
                                // We've been disconnected payloadFrom this endpoint. No more data can be
                                // sent or received.
                                Log.d(TAG, "onDisconnected: endpointId=" + endpointId);
                            }
                        }, Options.ADVERTISING)
                        .addOnSuccessListener(this::onSuccessAdvertising)
                        .addOnFailureListener(this::onFailureAdvertising));
    }

    private void disconnectConnection(@NonNull String endpointId) {
        Log.d(TAG, "disconnectConnection: endpointId=" + endpointId);
        connectionsClient().disconnectFromEndpoint(endpointId);
    }

    private Single<Payload> sendPayload(String endpointId, Packet packet) {
        Log.d(TAG, "sendPayload: endpointId=" + endpointId + " " + packet);
        return Single.create(e -> {
            Payload payload = NearbyUtil.payloadFrom(packet);
            connectionsClient().sendPayload(endpointId, payload)
                    .addOnSuccessListener(aVoid -> onSuccessSendPayload(e, endpointId, payload))
                    .addOnFailureListener(Runnable::run, this::onFailureSendPayload);
        });
    }

    private Single<String> sendMenuItems(String endPointId) {
        Log.d(TAG, "sendMenuItems: endPointId=" + endPointId);
        return MenuItemRepository.from(activity).getMenuItemsOfStore(store)
                .map(Packet::new)
                .flatMap(packet -> sendPayload(endPointId, packet))
                .map(payload -> String.valueOf(payload.getId()));
    }

    private void onSuccessSendPayload(SingleEmitter<Payload> emitter, String endpointId, Payload payload) {
        Log.d(TAG, "onSuccessSendPayload: endpointId=" + endpointId);
        emitter.onSuccess(payload);
    }

    private void onFailureSendPayload(Exception e) {
        Log.e(TAG, "onFailureSendPayload: ", e);
    }

    private Observable<CombinedResult<String, Payload>> acceptConnection(String endpointId) {
        return Observable.create(e -> {
            Log.d(TAG, "acceptConnection: endpointId=" + endpointId);
            connectionsClient().acceptConnection(endpointId,
                    new PayloadCallback() {
                        @Override
                        public void onPayloadReceived(@NonNull String endpointId1, @NonNull Payload payload) {
                            Log.d(TAG, "onPayloadReceived: endpointId=" + endpointId1
                                    + " Payload["
                                    + " id=" + payload.getId()
                                    + " type=" + payload.getType()
                                    + "]");

                            e.onNext(new CombinedResult<>(endpointId1, payload));
                        }

                        @Override
                        public void onPayloadTransferUpdate(@NonNull String payload, @NonNull PayloadTransferUpdate update) {

//                            Log.d(TAG, "onPayloadTransferUpdate: payload=" + payload
//                                    + " PayloadTransferUpdate["
//                                    + " payloadId=" + update.getPayloadId()
//                                    + " bytesTransferred=" + update.getBytesTransferred()
//                                    + " status=" + update.getStatus()
//                                    + " totalBytes=" + update.getTotalBytes()
//                                    + "]");
                        }
                    })
                    .addOnSuccessListener(this::onSuccessAcceptConnection)
                    .addOnFailureListener(this::onFailureAcceptConnection);
        });
    }

    @SuppressWarnings("unchecked")
    private Observable<String> processPayload(final CombinedResult<String, Payload> combinedResult) {
        Payload payload = combinedResult.r;
        String endPointId = combinedResult.t;

        Packet packet = new Packet(NearbyUtil.bytesFrom(payload));
        Log.d(TAG, "processPayload: " + packet);

        PacketType packetType = packet.getPacketType();
        switch (packetType) {
            case MENU_ORDER:
                Packet<MenuOrder> menuOrderPacket = (Packet<MenuOrder>) packet;
                Packet<Response> responsePacket = new Packet<>(Response.SUCCESS);
                return Single.just(menuOrderPacket)
                        .doOnSuccess(unUsed ->
                                sendPayload(endPointId, responsePacket)
                                        .subscribe()
                        )
                        .map(Packet::get)
                        .map(MenuOrder::toString)
                        .toObservable();

            case REQUEST:
                if (packet.get() instanceof Request) {
                    CombinedResult<String, Packet<Request>> result = new CombinedResult<>(endPointId, (Packet<Request>) packet);
                    return processOnRequest(result);
                }
                break;
        }
        throw new UnsupportedOperationException();
    }

    private Observable<String> processOnRequest(CombinedResult<String, Packet<Request>> result) {
        Log.d(TAG, "processOnRequest: " + result);
        Request request = result.r.get();
        String endPointId = result.t;
        Single<String> task;
        switch (request) {
            case REQ_MENU_ITEM_LIST:
                task = sendMenuItems(endPointId);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        return Observable.create(e -> task.doOnSuccess(e::onNext).subscribe());
    }

    @SuppressWarnings("unused")
    private void onSuccessAcceptConnection(Void unusedResult) {
        Log.d(TAG, "onSuccessAcceptConnection: ");
    }

    private void onFailureAcceptConnection(Exception e) {
        Log.e(TAG, "onFailureAcceptConnection: ", e);
    }

    public void stopAdvertising() {
        connectionsClient().stopAdvertising();
    }

    private ConnectionsClient connectionsClient() {
        return Nearby.getConnectionsClient(activity);
    }

}

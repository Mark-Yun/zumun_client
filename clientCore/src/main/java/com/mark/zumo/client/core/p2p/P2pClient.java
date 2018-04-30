package com.mark.zumo.client.core.p2p;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class P2pClient {

    private static final String TAG = "P2pClient";
    private static final long TIME_OUT = 5000;

    private Activity activity;
    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {
            onBleSignalChanged(message, bleSignal);
        }

        @Override
        public void onDistanceChanged(Message message, Distance distance) {
            onDistanceChanged(message, distance);
        }

        @Override
        public void onFound(Message message) {
            onFound(message);
        }

        @Override
        public void onLost(Message message) {
            onLost(message);
        }
    };

    public P2pClient(Activity activity) {
        this.activity = activity;
    }

    private void onBleSignalChanged(Message message, BleSignal bleSignal) {
        Log.d(TAG, "onBleSignalChanged: " + NearbyUtil.convertString(message) + NearbyUtil.convertString(bleSignal));
    }

    private void onDistanceChanged(Message message, Distance distance) {
        Log.d(TAG, "onDistanceChanged: " + NearbyUtil.convertString(message) + NearbyUtil.convertString(distance));
    }

    private void onFound(Message message) {
        Log.d(TAG, "onFound: " + NearbyUtil.convertString(message));
    }

    private void onLost(Message message) {
        Log.d(TAG, "onLost: " + NearbyUtil.convertString(message));
    }

    private MessagesClient messageClient() {
        return Nearby.getMessagesClient(activity);
    }

    public Observable<Store> findStore() {
        return Observable.create(e -> {
            messageClient().subscribe(messageListener);
        });
    }

    public Observable<List<MenuItem>> loadMenuItemList(Store store) {
        return Observable.create(e -> {

        });
    }
}

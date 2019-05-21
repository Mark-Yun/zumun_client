/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.p2p;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.database.entity.user.GuestUser;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.p2p.P2pServer;
import com.mark.zumo.client.store.R;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 5. 2.
 */

public class P2pDebugActivity extends Activity {

    private static final String TAG = "P2pDebugActivity";
    private TextView console;

    private P2pServer p2pServer;
    private P2pClient p2pClient;

    private GuestUser currentUser;
    private Store testStore;
    private MenuOrder testMenuOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2p_debug_activity);

        p2pClient = P2pClient.INSTANCE;
        p2pServer = new P2pServer(this, testStore);
        inflateView();
    }

    private void inflateView() {
        console = findViewById(R.id.console);

        Button publishStart = findViewById(R.id.publish_start);
        Button publishStop = findViewById(R.id.publish_stop);
        Button subscribeStart = findViewById(R.id.subscribe_start);
        Button subscribeStop = findViewById(R.id.subscribe_stop);

        publishStart.setOnClickListener(this::startPublish);
        publishStop.setOnClickListener(this::stopPublish);
        subscribeStart.setOnClickListener(this::startSubscribe);
        subscribeStop.setOnClickListener(this::stopSubscribe);

        Button startAdvertising = findViewById(R.id.start_advertising);
        Button stopAdvertising = findViewById(R.id.stop_advertising);
        Button acquireMenuItems = findViewById(R.id.acquire_menu_items);
        Button stopDiscovery = findViewById(R.id.stop_discovery);

        startAdvertising.setOnClickListener(this::startAdvertising);
        stopAdvertising.setOnClickListener(this::stopAdvertising);
        acquireMenuItems.setOnClickListener(this::acquireMenuItems);
        stopDiscovery.setOnClickListener(this::stopDiscovery);

        Button sendOrder = findViewById(R.id.send_order);
        sendOrder.setOnClickListener(this::sendOrder);
    }

    private void sendOrder(View v) {
    }

    private void startPublish(View v) {
        p2pServer.publish();
        initConsole("Publishing... " + testStore);
    }

    private void stopPublish(View v) {
        p2pServer.unpublish();
        initConsole("Unpublished");
    }

    private void startSubscribe(View v) {
        initConsole(currentUser + "\n");
    }

    private String getSubscribeConsoleText(Set<Store> stores) {
        StringBuilder ret = new StringBuilder("Subscribing..." + currentUser + "\n");
        for (Store store : stores) {
            ret.append(store).append("\n");
        }
        return ret.toString();
    }

    private void stopSubscribe(View v) {
        initConsole("unsubscribed");
    }

    private void startAdvertising(View view) {
        initConsole("Advertising...");
        p2pServer.findCustomer()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::updateConsole)
                .subscribe();
    }

    private void stopAdvertising(View view) {
        p2pServer.stopAdvertising();
        initConsole("Stopped Advertising");
    }

    private void acquireMenuItems(View view) {
    }

    @NonNull
    private String getDiscoveryConsoleText(String string) {
        return "Discovering...\n" + string;
    }

    private void stopDiscovery(View view) {
        p2pClient.stopDiscovery();
    }

    private void updateConsole(String text) {
        String newText = console.getText() + text + "n";
        console.setText(newText);
    }

    private void initConsole(String text) {
        String newText = text + "\n";
        console.setText(newText);
    }
}

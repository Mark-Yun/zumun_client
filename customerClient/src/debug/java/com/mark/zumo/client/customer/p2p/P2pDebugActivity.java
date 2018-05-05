package com.mark.zumo.client.customer.p2p;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Order;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.CustomerUser;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.p2p.P2pServer;
import com.mark.zumo.client.customer.R;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 2.
 */

public class P2pDebugActivity extends Activity {

    private static final String TAG = "P2pDebugActivity";
    private TextView console;

    private P2pServer p2pServer;
    private P2pClient p2pClient;

    private CustomerUser currentUser;
    private Store testStore;
    private Order testOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2p_debug_activity);

        currentUser = DebugUtil.testCustomerUser();
        testStore = DebugUtil.testStore();
        testOrder = DebugUtil.testOrder();

        p2pClient = new P2pClient(this, currentUser);
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
        p2pClient.sendOrder(testOrder, testStore.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateConsole);
        initConsole("Send Order...");
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
        p2pClient = new P2pClient(this, currentUser);
        p2pClient.subscribe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stores -> initConsole(getSubscribeConsoleText(stores)));
        initConsole(currentUser + "\n");
    }

    private String getSubscribeConsoleText(Set<Store> stores) {
        String ret = "Subscribing..." + currentUser + "\n";
        for (Store store : stores) {
            ret += store + "\n";
        }
        return ret;
    }

    private void stopSubscribe(View v) {
        p2pClient.unsubscribe();
        initConsole("unsubscribed");
    }

    private void startAdvertising(View view) {
        initConsole("Advertising...");
        p2pServer.findCustomer(testStore)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateConsole);
    }

    private void stopAdvertising(View view) {
        p2pServer.stopAdvertising();
        initConsole("Stopped Advertising");
    }

    private void acquireMenuItems(View view) {
        p2pClient.acquireMenuItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(menuItemList -> {
                    for (MenuItem menuItem : menuItemList) {
                        updateConsole(menuItem + "\n");
                    }
                });

        initConsole("Discovering...");
    }

    @NonNull
    private String getDiscoveryConsoleText(String string) {
        return "Discovering...\n" + string;
    }

    private void stopDiscovery(View view) {
        p2pClient.stopDiscovery();
    }

    private void updateConsole(String text) {
        String newText = console.getText() + text;
        console.setText(newText);
    }

    private void initConsole(String text) {
        console.setText(text);
    }
}

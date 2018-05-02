package com.mark.zumo.client.customer.p2p;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mark.zumo.client.R;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.CustomerUser;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.p2p.P2pServer;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 2.
 */

public class P2pMessageDebugActivity extends Activity {

    private TextView console;
    private Store store = new Store(5, Build.MODEL, 0, 0, 31, 31);
    private CustomerUser customerUser = new CustomerUser(0, Build.MODEL, 0);

    private P2pServer p2pServer;
    private P2pClient p2pClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2p_message_debug_activity);

        inflateView();
    }

    private void inflateView() {
        console = findViewById(R.id.console);

        Button publishStart = findViewById(R.id.publish_start);
        Button publishStop = findViewById(R.id.publish_stop);
        Button subscribeStart = findViewById(R.id.subscribe_start);
        Button subscribeStop = findViewById(R.id.subscribe_stop);

        publishStart.setOnClickListener(this::onClickPublishStart);
        publishStop.setOnClickListener(this::onClickPublishStop);
        subscribeStart.setOnClickListener(this::onClickSubscribeStart);
        subscribeStop.setOnClickListener(this::onClickSubscribeStop);
    }

    private void onClickPublishStart(View v) {
        p2pServer = new P2pServer(this, store);
        p2pServer.publish();
        console.setText("Publishing... " + store);
    }

    private void onClickPublishStop(View v) {
        p2pServer.unpublish();
        console.setText("Unpublished");
    }

    private void onClickSubscribeStart(View v) {
        p2pClient = new P2pClient(this, customerUser);
        p2pClient.subscribe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stores -> console.setText(getSubscribeConsoleText(stores)));
        console.setText("Subscribing...\n");
    }

    private String getSubscribeConsoleText(Set<Store> stores) {
        String ret = "Subscribing...\n";
        for (Store store : stores) {
            ret += store + "\n";
        }
        return ret;
    }

    private void onClickSubscribeStop(View v) {
        p2pClient.unsubscribe();
        console.setText("unsubscribed");
    }
}

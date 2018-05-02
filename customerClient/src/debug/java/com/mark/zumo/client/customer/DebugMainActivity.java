package com.mark.zumo.client.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.mark.zumo.client.R;
import com.mark.zumo.client.customer.p2p.P2pConnectionDebugActivity;
import com.mark.zumo.client.customer.p2p.P2pMessageDebugActivity;

/**
 * Created by mark on 18. 5. 2.
 */

public class DebugMainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_main_activity);

        Button p2pConnectionDebug = findViewById(R.id.p2p_connection_debug_activity);
        Button p2pMessageDebug = findViewById(R.id.p2p_message_debug_activity);

        p2pConnectionDebug.setOnClickListener(this::onClickConnectionDebug);
        p2pMessageDebug.setOnClickListener(this::onClickMessageDebug);
    }

    private void onClickMessageDebug(View v) {
        startDebugActivity(P2pMessageDebugActivity.class);
    }

    private void onClickConnectionDebug(View v) {
        startDebugActivity(P2pConnectionDebugActivity.class);
    }

    private void startDebugActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}

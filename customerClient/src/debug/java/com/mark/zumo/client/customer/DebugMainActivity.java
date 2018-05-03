package com.mark.zumo.client.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.mark.zumo.client.customer.p2p.P2pDebugActivity;

/**
 * Created by mark on 18. 5. 2.
 */

public class DebugMainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_main_activity);

        Button p2pDebug = findViewById(R.id.p2p_debug_activity);

        p2pDebug.setOnClickListener(this::onClickP2pDebug);
    }

    private void onClickP2pDebug(View v) {
        startDebugActivity(P2pDebugActivity.class);
    }

    private void startDebugActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}

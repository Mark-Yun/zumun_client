package com.mark.zumo.client.core.p2p;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mark.zumo.client.core.R;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 2.
 */

public class P2pDebugActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2p_debug_activity);
        ButterKnife.bind(this);
    }
}

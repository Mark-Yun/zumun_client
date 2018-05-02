package com.mark.zumo.client.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 2.
 */

public class DebugMainActivity extends Activity {

    @BindView(R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_main_activity);
        ButterKnife.bind(this);
    }
}

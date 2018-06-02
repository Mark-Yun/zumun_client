/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.view.Window;
import android.widget.Toast;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.Navigator;
import com.wonderkiln.blurkit.BlurLayout;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 4. 30.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.blur_filter) BlurLayout blurFilter;

    private long backKeyPressedTimeMills;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inflateMenuFragment();

        Navigator.addBlurFilter(blurFilter);
    }

    private void inflateMenuFragment() {
        Fragment mainBodyFragment = Fragment.instantiate(this, MainBodyFragment.class.getName());
        Fragment mainHeaderFragment = Fragment.instantiate(this, MainHeaderFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_body_fragment, mainBodyFragment)
                .add(R.id.main_header_fragment, mainHeaderFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Navigator.setBlurLayoutVisible(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Navigator.removeBlurFilter(blurFilter);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - backKeyPressedTimeMills > TimeUnit.SECONDS.toMillis(2)) {
                backKeyPressedTimeMills = currentTime;
                Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
        super.onBackPressed();
    }
}
/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.core.view.Navigator;
import com.mark.zumo.client.customer.R;
import com.wonderkiln.blurkit.BlurLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 4. 30.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.blur_filter) BlurLayout blurFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inflateMenuFragment();

        Navigator.addBlurFilter(blurFilter);
    }

    private void inflateMenuFragment() {
        Fragment mainBodyFragment = Fragment.instantiate(this, MainBodyFragment.class.getName());
        Fragment mainHeaderFragment = Fragment.instantiate(this, MainHeaderFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_body_fragment, mainBodyFragment)
                .replace(R.id.main_header_fragment, mainHeaderFragment)
                .commitAllowingStateLoss();
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
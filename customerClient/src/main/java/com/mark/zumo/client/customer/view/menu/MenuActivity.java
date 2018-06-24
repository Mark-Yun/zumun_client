/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.core.view.Navigator;
import com.mark.zumo.client.customer.R;
import com.wonderkiln.blurkit.BlurLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 14.
 */
public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.blur_filter) BlurLayout blurFilter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        inflateMenuFragment();
        Navigator.addBlurFilter(blurFilter);
    }

    private void inflateMenuFragment() {
        Fragment fragment = Fragment.instantiate(this, MenuFragment.class.getName(), getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_fragment, fragment)
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

}
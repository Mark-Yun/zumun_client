/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.fragment.SettingPreferenceFragment;
import com.mark.zumo.client.store.view.setting.fragment.StoreProfileSettingFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 25.
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        inflateDefaultFragment();
    }

    private void inflateDefaultFragment() {
        Fragment settingPreferenceFragment = Fragment.instantiate(this, SettingPreferenceFragment.class.getName());
        Fragment storeProfileSettingFragment = Fragment.instantiate(this, StoreProfileSettingFragment.class.getName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.preference_fragment, settingPreferenceFragment)
                .replace(R.id.setting_main_fragment, storeProfileSettingFragment)
                .commit();
    }
}

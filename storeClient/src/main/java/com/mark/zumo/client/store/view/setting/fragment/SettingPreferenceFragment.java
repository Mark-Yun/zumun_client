/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;

import com.mark.zumo.client.store.R;

import java.util.Objects;

/**
 * Created by mark on 18. 6. 25.
 */
public class SettingPreferenceFragment extends BasePreferenceFragmentCompat {

    private Preference selectedPreference;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen_setting);
    }

    @Override
    public boolean onPreferenceTreeClick(final Preference preference) {
        selectedPreference = preference;
        Fragment fragment = Fragment.instantiate(getContext(), preference.getFragment());

        Objects.requireNonNull(getFragmentManager())
                .beginTransaction()
                .replace(R.id.setting_main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        return super.onPreferenceTreeClick(preference);
    }
}

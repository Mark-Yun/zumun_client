/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.create;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuCreatePreferenceFragment extends PreferenceFragmentCompat {

    private final static String TAG = "MenuCreatePreferenceFragment";

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen_menu_detail_setting);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        inflate();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void inflate() {
        EditTextPreference menuNamePreference = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_name));
        EditTextPreference menuPricePreference = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_price));
//
//        menuNamePreference.setSummary(menu.name);
//        menuPricePreference.setSummary(String.valueOf(menu.price));
    }
}

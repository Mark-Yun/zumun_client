/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuDetailPreferenceFragment extends PreferenceFragmentCompat {

    private MenuSettingViewModel menuSettingViewModel;
    private String menuUuid;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen_menu_detail_setting);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuUuid = getArguments().getString(MenuDetailSettingFragment.KEY_MENU_UUID);
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        inflateMenuInformation();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void inflateMenuInformation() {
        menuSettingViewModel.menuFromDisk(menuUuid).observe(this, this::onLoadMenu);
    }

    private void onLoadMenu(Menu menu) {
        EditTextPreference menuName = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_name));
        EditTextPreference menuPrice = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_price));

        menuName.setSummary(menu.name);
        menuName.setOnPreferenceChangeListener(this::onMenuNameChanged);

        menuPrice.setSummary(String.valueOf(menu.price));
        menuPrice.setOnPreferenceChangeListener(this::onMenuPriceChanged);

    }

    private boolean onMenuNameChanged(final Preference preference, final Object newValue) {
        menuSettingViewModel.updateMenuName(menuUuid, (String) newValue).observe(this, this::onLoadMenu);
        return true;
    }

    private boolean onMenuPriceChanged(final Preference preference, final Object newValue) {
        menuSettingViewModel.updateMenuPrice(menuUuid, (int) newValue).observe(this, this::onLoadMenu);
        return true;
    }
}

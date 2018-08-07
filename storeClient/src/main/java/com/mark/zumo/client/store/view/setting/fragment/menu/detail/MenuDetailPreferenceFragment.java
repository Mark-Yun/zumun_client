/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        menuSettingViewModel.categoryList().observe(this, this::onLoadCategory);
    }

    private void onLoadMenu(Menu menu) {
        EditTextPreference menuName = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_name));
        MultiSelectListPreference menuCategory = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_category));
        EditTextPreference menuPrice = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_price));

        menuName.setSummary(menu.name);
        menuName.setOnPreferenceChangeListener(this::onMenuNameChanged);

//        menuCategory.setSummary(menu.categoryName);
//        menuCategory.setOnPreferenceChangeListener(this::onMenuCategoryChanged);

        menuPrice.setSummary(String.valueOf(menu.price));
        menuPrice.setOnPreferenceChangeListener(this::onMenuPriceChanged);
    }

    private void onLoadCategory(List<MenuCategory> categoryList) {
        MultiSelectListPreference menuCategoryPreference = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_category));
        Observable.fromIterable(categoryList)
                .sorted((m1, m2) -> m2.seqNum - m1.seqNum)
                .map(menuCategory -> menuCategory.name)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuCategoryPreference::setEntries)
                .subscribe();

        Observable.fromIterable(categoryList)
                .sorted((m1, m2) -> m2.seqNum - m1.seqNum)
                .map(menuCategory -> menuCategory.uuid)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuCategoryPreference::setEntryValues)
                .subscribe();
    }

    private boolean onMenuNameChanged(final Preference preference, final Object newValue) {
        menuSettingViewModel.updateMenuName(menuUuid, (String) newValue).observe(this, this::onLoadMenu);
        return true;
    }

    private boolean onMenuPriceChanged(final Preference preference, final Object newValue) {
        try {
            menuSettingViewModel.updateMenuPrice(menuUuid, Integer.parseInt((String) newValue)).observe(this, this::onLoadMenu);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), R.string.error_message_insert_as_number, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private boolean onMenuCategoryChanged(final Preference preference, final Object newValue) {
        menuSettingViewModel.updateMenuCategory(menuUuid, (String) newValue).observe(this, this::onLoadMenu);
        return true;
    }
}

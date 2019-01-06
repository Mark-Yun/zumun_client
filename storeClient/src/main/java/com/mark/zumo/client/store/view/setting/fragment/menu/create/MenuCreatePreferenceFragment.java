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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuCreatePreferenceFragment extends PreferenceFragmentCompat {

    private final static String TAG = "MenuCreatePreferenceFragment";

    private MenuSettingViewModel menuSettingViewModel;
    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private MultiSelectListPreference menuCategoryPreference;
    private MultiSelectListPreference menuOptionCategoryPreference;

    private Menu.Builder menuBuilder;
    private Map<String, MenuCategory> menuCategoryMap;
    private Map<String, MenuOptionCategory> menuOptionCategoryMap;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
        menuBuilder = new Menu.Builder();
        menuCategoryMap = new HashMap<>();
        menuOptionCategoryMap = new HashMap<>();
    }

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

        menuCategoryPreference = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_category));
        menuOptionCategoryPreference = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_option));

        menuSettingViewModel.getMenuCategoryList().observe(this, this::onLoadMenuCategoryList);
        menuOptionSettingViewModel.getMenuOptionCategoryList().observe(this, this::onLoadMenuOptionCategoryList);

        menuNamePreference.setOnPreferenceChangeListener(this::onNameChanged);
        menuPricePreference.setOnPreferenceChangeListener(this::onPriceChanged);
    }

    public Menu.Builder getMenuBuilder() {
        return menuBuilder;
    }

    public Set<String> getSelectedMenuCategoryUuid() {
        return menuCategoryPreference.getValues();
    }

    public Set<String> getSelectedMenuOptionCategoryUuid() {
        return menuOptionCategoryPreference.getValues();
    }

    private boolean onNameChanged(Preference preference, Object newValue) {
        String name = (String) newValue;
        menuBuilder.setName(name);
        preference.setSummary(name);
        return true;
    }

    private boolean onPriceChanged(Preference preference, Object newValue) {
        String price = (String) newValue;
        menuBuilder.setPrice(Integer.parseInt(price));
        preference.setSummary(price);
        return true;
    }

    private boolean onMenuCategoryChanged(Preference preference, Object newValue) {
        Set<String> menuCategoryUuidSet = (Set<String>) newValue;

        StringBuilder summaryBuilder = new StringBuilder();
        for (String menuCategoryUuid : menuCategoryUuidSet) {
            if (summaryBuilder.length() > 0) {
                summaryBuilder.append(", ");
            }

            summaryBuilder.append(menuCategoryMap.get(menuCategoryUuid).name);
        }

        preference.setSummary(summaryBuilder.toString());

        return true;
    }

    private boolean onMenuOptionCategoryChanged(Preference preference, Object newValue) {
        Set<String> menuOptionCategoryUuidList = (Set<String>) newValue;

        StringBuilder summaryBuilder = new StringBuilder();
        for (String menuOptionCategoryUuid : menuOptionCategoryUuidList) {
            if (summaryBuilder.length() > 0) {
                summaryBuilder.append(", ");
            }

            summaryBuilder.append(menuOptionCategoryMap.get(menuOptionCategoryUuid).name);
        }

        preference.setSummary(summaryBuilder.toString());

        return true;
    }

    private void onLoadMenuCategoryList(List<MenuCategory> menuCategoryList) {
        Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.name)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .doOnSuccess(menuCategoryPreference::setEntries)
                .subscribe();

        Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.uuid)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .doOnSuccess(menuCategoryPreference::setEntryValues)
                .subscribe();

        Observable.fromIterable(menuCategoryList)
                .toMap(menuCategory -> menuCategory.uuid)
                .doOnSuccess(menuCategoryMap::putAll)
                .subscribe();

        menuCategoryPreference.setOnPreferenceChangeListener(this::onMenuCategoryChanged);
    }

    private void onLoadMenuOptionCategoryList(List<MenuOptionCategory> menuOptionCategoryList) {
        Observable.fromIterable(menuOptionCategoryList)
                .map(menuOptionCategory -> menuOptionCategory.name)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .doOnSuccess(menuOptionCategoryPreference::setEntries)
                .subscribe();

        Observable.fromIterable(menuOptionCategoryList)
                .map(menuOptionCategory -> menuOptionCategory.uuid)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .doOnSuccess(menuOptionCategoryPreference::setEntryValues)
                .subscribe();

        Observable.fromIterable(menuOptionCategoryList)
                .toMap(menuOptionCategory -> menuOptionCategory.uuid)
                .doOnSuccess(menuOptionCategoryMap::putAll)
                .subscribe();

        menuOptionCategoryPreference.setOnPreferenceChangeListener(this::onMenuOptionCategoryChanged);
    }
}

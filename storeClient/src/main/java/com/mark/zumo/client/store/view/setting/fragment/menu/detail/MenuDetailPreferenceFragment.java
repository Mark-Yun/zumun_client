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
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuDetailPreferenceFragment extends PreferenceFragmentCompat {

    private final static String TAG = "MenuDetailPreferenceFragment";

    private MenuSettingViewModel menuSettingViewModel;
    private MenuOptionSettingViewModel menuOptionSettingViewModel;

    private String menuUuid;
    private MultiSelectListPreference menuCategoryPreference;
    private MultiSelectListPreference menuOptionCategoryPreference;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen_menu_detail_setting);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuUuid = getArguments().getString(MenuDetailSettingFragment.KEY_MENU_UUID);
        menuSettingViewModel = ViewModelProviders.of(getActivity()).get(MenuSettingViewModel.class);
        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        inflateMenuInformation();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void inflateMenuInformation() {
        menuCategoryPreference = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_category));
        menuOptionCategoryPreference = (MultiSelectListPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_option));

        menuSettingViewModel.getMenuFromDisk(menuUuid).observe(this, this::onLoadMenu);
        menuSettingViewModel.getMenuCategoryList().observe(this, this::onLoadWholeMenuCategory);
        menuSettingViewModel.getMenuCategoryListByMenuUuid(menuUuid).observe(this, this::onLoadSelectedMenuCategory);
        menuOptionSettingViewModel.getMenuOptionCategoryList().observe(this, this::onLoadSelectedMenuOptionCategoryList);
        menuOptionSettingViewModel.getMenuOptionCategoryListByMenuUuid(menuUuid).observe(this, this::onLoadWholeMenuOptionCategoryList);
    }

    private void onLoadSelectedMenuOptionCategoryList(List<MenuOptionCategory> menuOptionCategoryList) {
        Set<String> valueSet = new HashSet<>();
        StringBuilder summary = new StringBuilder();
        for (MenuOptionCategory menuOptionCategory : menuOptionCategoryList) {
            if (summary.length() > 0) {
                summary.append(", ");
            }
            valueSet.add(menuOptionCategory.uuid);
            summary.append(menuOptionCategory.name);
        }

        menuOptionCategoryPreference.setValues(valueSet);
        menuOptionCategoryPreference.setSummary(summary.toString());
        menuOptionCategoryPreference.setOnPreferenceChangeListener(this::onMenuOptionCategoryChanged);
    }

    private void onLoadWholeMenuOptionCategoryList(List<MenuOptionCategory> menuOptionCategoryList) {
        Observable.fromIterable(menuOptionCategoryList)
                .sorted((m1, m2) -> m1.seqNum - m2.seqNum)
                .map(menuCategory -> menuCategory.name)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuOptionCategoryPreference::setEntries)
                .subscribe();

        Observable.fromIterable(menuOptionCategoryList)
                .sorted((m1, m2) -> m1.seqNum - m2.seqNum)
                .map(menuCategory -> menuCategory.uuid)
                .toList()
                .map(list -> list.toArray(new String[]{}))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuOptionCategoryPreference::setEntryValues)
                .subscribe();
    }

    private void onLoadMenu(Menu menu) {
        EditTextPreference menuNamePreference = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_name));
        EditTextPreference menuPricePreference = (EditTextPreference) findPreference(getString(R.string.preference_key_menu_detail_menu_price));

        menuPricePreference.setOnPreferenceChangeListener(this::onMenuPriceChanged);
        menuNamePreference.setOnPreferenceChangeListener(this::onMenuNameChanged);

        menuNamePreference.setSummary(menu.name);
        menuPricePreference.setSummary(String.valueOf(menu.price));
    }

    private void onLoadSelectedMenuCategory(List<MenuCategory> categoryList) {

        Set<String> valueSet = new HashSet<>();
        StringBuilder summary = new StringBuilder();
        for (MenuCategory menuCategory : categoryList) {
            if (summary.length() > 0) {
                summary.append(", ");
            }
            valueSet.add(menuCategory.uuid);
            summary.append(menuCategory.name);
        }
        menuCategoryPreference.setValues(valueSet);
        menuCategoryPreference.setSummary(summary.toString());
        menuCategoryPreference.setOnPreferenceChangeListener(this::onMenuCategoryChanged);
    }

    private void onLoadWholeMenuCategory(List<MenuCategory> categoryList) {
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
        Set<String> newCategorySet = (Set<String>) newValue;
        menuSettingViewModel.updateMenuCategoriesOfMenu(menuUuid, newCategorySet)
                .observe(this, this::onLoadSelectedMenuCategory);
        return true;
    }

    private boolean onMenuOptionCategoryChanged(final Preference preference, final Object newValue) {
        Set<String> newMenuOptionCategorySet = (Set<String>) newValue;
        menuOptionSettingViewModel.updateMenuOptionCategoriesOfMenu(menuUuid, newMenuOptionCategorySet)
                .observe(this, this::onLoadSelectedMenuOptionCategoryList);
        return true;
    }

    private void refreshCategoryList() {
        menuSettingViewModel.loadMenuListByCategory();
    }
}

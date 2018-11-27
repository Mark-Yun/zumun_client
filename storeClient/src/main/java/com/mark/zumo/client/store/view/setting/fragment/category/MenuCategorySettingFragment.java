/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 27.
 */
public class MenuCategorySettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_category, container, false);
        ButterKnife.bind(this, view);
        inflateFragments();
        return view;
    }

    private void inflateFragments() {
        MenuCategorySettingCategoryListFragment categoryListFragment = (MenuCategorySettingCategoryListFragment) Fragment.instantiate(getContext(), MenuCategorySettingCategoryListFragment.class.getName());
        MenuCategorySettingMenuListFragment menuListFragment = (MenuCategorySettingMenuListFragment) Fragment.instantiate(getContext(), MenuCategorySettingMenuListFragment.class.getName());

        categoryListFragment.setSelectMenuCategoryLister(menuListFragment::onSelectMenuCategory);

        getFragmentManager().beginTransaction()
                .replace(R.id.category_list, categoryListFragment)
                .replace(R.id.menu_list, menuListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }
}

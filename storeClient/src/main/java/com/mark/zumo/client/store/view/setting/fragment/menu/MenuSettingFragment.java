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

package com.mark.zumo.client.store.view.setting.fragment.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.fragment.menu.create.MenuCreateSettingFragment;
import com.mark.zumo.client.store.view.setting.fragment.menu.detail.MenuDetailSettingFragment;
import com.mark.zumo.client.store.view.setting.fragment.menu.menulist.MenuSettingMenuListFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 25.
 */
public class MenuSettingFragment extends Fragment {

    private static final String TAG = "MenuSettingFragment";
    private MenuSettingMenuListFragment menuSettingMenuListFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        menuSettingMenuListFragment = ((MenuSettingMenuListFragment) Fragment.instantiate(getContext(), MenuSettingMenuListFragment.class.getName()))
                .onMenuSelect(this::onSelectMenu)
                .onMenuCreateRequest(this::onRequestedMenuCreation);

        getFragmentManager().beginTransaction()
                .replace(R.id.menu_list_fragment, menuSettingMenuListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void onSelectMenu(Menu menu) {
        MenuDetailSettingFragment menuDetailSettingFragment = MenuDetailSettingFragment.newInstance(menu.uuid)
                .onMenuUpdated(this::onUpdateMenu);

        getFragmentManager().beginTransaction()
                .replace(R.id.menu_detail_fragment, menuDetailSettingFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void onRequestedMenuCreation() {
        MenuCreateSettingFragment menuDetailSettingFragment = ((MenuCreateSettingFragment) Fragment.instantiate(getContext(), MenuCreateSettingFragment.class.getName()))
                .onCreateMenu(this::onCreateMenu);

        getFragmentManager().beginTransaction()
                .replace(R.id.menu_detail_fragment, menuDetailSettingFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void onCreateMenu(Menu menu) {
        menuSettingMenuListFragment.onMenuCreateComplete(menu);
    }

    private void onUpdateMenu(Menu menu) {
        menuSettingMenuListFragment.onMeuUpdate(menu);
    }
}

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

package com.mark.zumo.client.store.view.setting.fragment.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuDetailSettingFragment extends Fragment {

    public final static String KEY_MENU_UUID = "menu_uuid";

    @BindView(R.id.image) AppCompatImageView image;

    private MenuSettingViewModel menuSettingViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_detail_setting, container, false);
        ButterKnife.bind(this, view);

        inflatePreferenceFragment();
        inflateMenuImage();

        return view;
    }

    private void inflatePreferenceFragment() {
        Fragment fragment = PreferenceFragmentCompat.instantiate(getActivity(), MenuDetailPreferenceFragment.class.getName(), getArguments());
        getFragmentManager().beginTransaction()
                .replace(R.id.menu_detail_preference_fragment, fragment)
                .commit();
    }

    private void inflateMenuImage() {
        menuSettingViewModel.menuFromDisk(getArguments().getString(KEY_MENU_UUID)).observe(this, this::onLoadMenu);
    }

    private void onLoadMenu(Menu menu) {
        GlideApp.with(this)
                .load(menu.imageUrl)
                .into(image);
    }
}

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

package com.mark.zumo.client.store.view.setting.fragment.option;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 25.
 */
public class MenuOptionSettingFragment extends Fragment {

    private static final String TAG = "MenuOptionSettingFragment";

    @BindView(R.id.menu_option_setting_header) ConstraintLayout menuOptionSettingHeader;
    @BindView(R.id.menu_option_recycler_view) RecyclerView menuOptionRecyclerView;
    @BindView(R.id.menu_option_detail_recycler_view) RecyclerView menuOptionDetailRecyclerView;
    @BindView(R.id.create_new_option) ConstraintLayout createNewOption;

    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private MenuOptionMenuCategoryAdapter menuOptionMenuCategoryAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_option, container, false);
        ButterKnife.bind(this, view);

        inflateMenuOptionList();
        inflateMenuOptionMenuList();
        return view;
    }

    private void inflateMenuOptionList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        menuOptionRecyclerView.setLayoutManager(layoutManager);
        menuOptionRecyclerView.setHasFixedSize(true);
        menuOptionRecyclerView.setNestedScrollingEnabled(false);

        MenuOptionAdapter menuOptionAdapter = new MenuOptionAdapter();
        menuOptionAdapter.setListener(this::onSelectMenuOption);
        menuOptionRecyclerView.setAdapter(menuOptionAdapter);

        menuOptionSettingViewModel.getMenuOptionList().observe(this, menuOptionAdapter::setMenuOptionList);
    }

    private void inflateMenuOptionMenuList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        menuOptionDetailRecyclerView.setLayoutManager(layoutManager);
        menuOptionDetailRecyclerView.setHasFixedSize(true);
        menuOptionDetailRecyclerView.setNestedScrollingEnabled(false);

        menuOptionMenuCategoryAdapter = new MenuOptionMenuCategoryAdapter();
        menuOptionDetailRecyclerView.setAdapter(menuOptionMenuCategoryAdapter);
    }

    private void onSelectMenuOption(List<MenuOption> menuOptionList) {
        Log.d(TAG, "onSelectMenuOption: name=" + menuOptionList.get(0).name);
        menuOptionSettingViewModel.getCategoryList().observe(this, menuOptionMenuCategoryAdapter::setMenuCategoryList);
    }
}

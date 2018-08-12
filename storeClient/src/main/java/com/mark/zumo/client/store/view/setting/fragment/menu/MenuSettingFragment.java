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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 25.
 */
public class MenuSettingFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private MenuSettingViewModel menuSettingViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuSettingViewModel = ViewModelProviders.of(getActivity()).get(MenuSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu, container, false);
        ButterKnife.bind(this, view);

        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, menuSettingViewModel, getFragmentManager());
        recyclerView.setAdapter(categoryAdapter);

        menuSettingViewModel.loadMenuCategoryList().observe(this, categoryAdapter::setCategoryList);
        menuSettingViewModel.getMenuListByCategory().observe(this, categoryAdapter::setMenuListMap);
    }

    @OnClick(R.id.add_menu)
    public void onViewClicked() {
    }
}

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
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
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        // specify an menuAdapter (see also next example)
        MenuAdapter menuAdapter = new MenuAdapter(getFragmentManager());
        recyclerView.setAdapter(menuAdapter);

        menuSettingViewModel.menuList().observe(this, menuAdapter::setMenuList);
    }

    @OnClick(R.id.add_menu)
    public void onViewClicked() {
    }
}

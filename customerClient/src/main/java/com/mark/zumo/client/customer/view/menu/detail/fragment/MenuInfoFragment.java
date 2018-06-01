/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuInfoFragment extends Fragment {

    @BindView(R.id.image) AppCompatImageView image;
    @BindView(R.id.name) TextView name;

    private MenuDetailViewModel menuDetailViewModel;
    private String menuUuid;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        menuUuid = Objects.requireNonNull(getArguments()).getString(MenuDetailActivity.KEY_MENU_UUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_info, container, false);
        ButterKnife.bind(this, view);
        inflateMenuInfo();
        return view;
    }

    private void inflateMenuInfo() {
        menuDetailViewModel.getMenu(menuUuid).observe(this, this::onLoadMenu);
    }

    private void onLoadMenu(Menu menu) {
        GlideApp.with(this).load(menu.imageUrl)
                .apply(GlideUtils.menuImageOptions())
                .transition(GlideUtils.menuTransitionOptions())
                .into(image);

        name.setText(menu.name);
    }
}

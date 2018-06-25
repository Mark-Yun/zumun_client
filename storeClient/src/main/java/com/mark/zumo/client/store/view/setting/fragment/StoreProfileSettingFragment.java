/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.SettingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 25.
 */
public class StoreProfileSettingFragment extends Fragment {

    @BindView(R.id.cover_image) AppCompatImageView coverImage;
    @BindView(R.id.thumbnail_image) AppCompatImageView thumbnailImage;
    @BindView(R.id.store_name) AppCompatTextView storeName;

    private SettingViewModel settingViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_profile_setting, container, false);
        ButterKnife.bind(this, view);

        inflateStoreProfileView();

        return view;
    }

    private void inflateStoreProfileView() {
        settingViewModel.getCurrentStore().observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        //TODO remove test data
        GlideApp.with(this)
                .load(R.drawable.data_1_hot)
                .into(coverImage);

        GlideApp.with(this)
                .load(R.drawable.data_1_hot)
                .apply(GlideUtils.storeImageOptions())
                .into(thumbnailImage);

        storeName.setText(store.name);
    }
}

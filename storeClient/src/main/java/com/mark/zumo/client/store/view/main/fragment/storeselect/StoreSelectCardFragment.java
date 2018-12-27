/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.main.fragment.storeselect;

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

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 27.
 */
public class StoreSelectCardFragment extends Fragment {

    static final String STORE_UUID = "store_uuid";

    @BindView(R.id.cover_image) AppCompatImageView coverImage;
    @BindView(R.id.thumbnail_image) AppCompatImageView thumbnailImage;
    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.store_phone_number) AppCompatTextView storePhoneNumber;
    @BindView(R.id.store_address) AppCompatTextView storeAddress;

    private StoreSelectListener listener;
    private String storeUuid;
    private MainViewModel mainViewModel;

    StoreSelectCardFragment setListener(final StoreSelectListener listener) {
        this.listener = listener;
        return this;
    }

    StoreSelectCardFragment setStoreUuid(final String storeUuid) {
        this.storeUuid = storeUuid;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_select_card, container, false);
        ButterKnife.bind(this, view);

        inflateStoreInfo();

        return view;
    }

    private void inflateStoreInfo() {
        mainViewModel.getStore(storeUuid).observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        if (store == null) {
            return;
        }

        storeName.setText(store.name);
        storePhoneNumber.setText(store.phoneNumber);
        storeAddress.setText(store.address);

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions())
                .into(coverImage);

        GlideApp.with(this)
                .load(store.thumbnailUrl)
                .apply(GlideUtils.storeImageOptions())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnailImage);
    }

    @OnClick(R.id.select_store)
    void onClickSelectStore() {
        listener.onSelectStore(storeUuid);
    }

    interface StoreSelectListener {
        void onSelectStore(String storeUuid);
    }
}

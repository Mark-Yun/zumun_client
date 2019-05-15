/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.menulist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;
import com.mark.zumo.client.store.viewmodel.StoreSettingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 17.
 */
public class MenuSettingMenuListFragment extends Fragment {

    private static final String TAG = "MenuSettingMenuListFragment";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.store_cover_image) AppCompatImageView storeCoverImage;
    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.store_thumbnail_image) AppCompatImageView storeThumbnailImage;
    @BindView(R.id.add_menu) FloatingActionButton addMenu;

    private MenuSettingViewModel menuSettingViewModel;
    private StoreSettingViewModel storeSettingViewModel;
    private MenuSelectListener menuSelectListener;
    private Runnable menuCreateRequestListener;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
        storeSettingViewModel = ViewModelProviders.of(this).get(StoreSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_menu_list, container, false);
        ButterKnife.bind(this, view);

        inflateStoreInfo();
        inflateRecyclerView();

        return view;
    }

    private void inflateStoreInfo() {
        storeSettingViewModel.getCurrentSessionStore().observe(this, this::onLoadStore);
    }

    public void onMenuCreateComplete(Menu menu) {
        menuSettingViewModel.loadCombinedMenuCategoryList();
    }

    public void onMeuUpdate(Menu menu) {
        menuSettingViewModel.loadCombinedMenuCategoryList();
    }

    private void onLoadStore(Store store) {
        storeName.setText(store.name);

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions())
                .into(storeCoverImage);


        GlideApp.with(this)
                .load(store.thumbnailUrl)
                .apply(GlideUtils.storeThumbnailImageOptions())
                .transition(GlideUtils.storeThumbnailTransitionOptions())
                .into(storeThumbnailImage);
    }

    public MenuSettingMenuListFragment onMenuSelect(MenuSelectListener menuSelectListener) {
        this.menuSelectListener = menuSelectListener;
        return this;
    }

    public MenuSettingMenuListFragment onMenuCreateRequest(Runnable menuCreateRequestListener) {
        this.menuCreateRequestListener = menuCreateRequestListener;
        return this;
    }

    private void inflateRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        CategoryAdapter categoryAdapter = new CategoryAdapter(menuSelectListener);
        recyclerView.setAdapter(categoryAdapter);
        menuSettingViewModel.getCombinedMenuCategoryList().observe(this, categoryAdapter::setCategoryList);
    }

    @OnClick(R.id.add_menu)
    public void onAddMenuClicked() {
        menuCreateRequestListener.run();
    }

    public interface MenuSelectListener extends CategoryAdapter.MenuSelectListener {
    }
}

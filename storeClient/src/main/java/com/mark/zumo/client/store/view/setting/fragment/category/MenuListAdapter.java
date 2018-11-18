/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 12.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {

    private LifecycleOwner lifecycleOwner;
    private MenuSettingViewModel menuSettingViewModel;

    private List<MenuDetail> menuDetailList;

    MenuListAdapter(final LifecycleOwner lifecycleOwner,
                    final MenuSettingViewModel menuSettingViewModel) {

        this.lifecycleOwner = lifecycleOwner;
        this.menuSettingViewModel = menuSettingViewModel;

        menuDetailList = new ArrayList<>();
    }

    void setMenuDetailList(List<MenuDetail> menuDetailList) {
        this.menuDetailList = menuDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_category_setting_menu_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        MenuDetail menuDetail = menuDetailList.get(position);
        menuSettingViewModel.getMenu(menuDetail.menuUuid)
                .observe(lifecycleOwner, menu -> {
                    if (menu == null) {
                        return;
                    }

                    viewHolder.name.setText(menu.name);
                });
    }

    @Override
    public int getItemCount() {
        return menuDetailList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;
        @BindView(R.id.remove_button) AppCompatImageButton removeButton;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

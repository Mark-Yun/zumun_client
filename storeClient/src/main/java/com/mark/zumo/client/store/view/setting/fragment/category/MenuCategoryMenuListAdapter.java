/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 12.
 */
public class MenuCategoryMenuListAdapter extends RecyclerView.Adapter<MenuCategoryMenuListAdapter.ViewHolder> {

    private LifecycleOwner lifecycleOwner;
    private MenuSettingViewModel menuSettingViewModel;

    private List<Menu> menuList;


    MenuCategoryMenuListAdapter(final LifecycleOwner lifecycleOwner,
                                final MenuSettingViewModel menuSettingViewModel) {

        this.lifecycleOwner = lifecycleOwner;
        this.menuSettingViewModel = menuSettingViewModel;

        menuList = new ArrayList<>();
    }

    void setMenuList(final List<Menu> menuList) {
        this.menuList = menuList;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Menu menu = menuList.get(position);
        holder.name.setText(menu.name);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

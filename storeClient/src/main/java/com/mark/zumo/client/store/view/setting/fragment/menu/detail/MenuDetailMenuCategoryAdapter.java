/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 1. 20.
 */
public class MenuDetailMenuCategoryAdapter extends RecyclerView.Adapter<MenuDetailMenuCategoryAdapter.ViewHolder> {

    private final List<MenuCategory> menuCategoryList;

    MenuDetailMenuCategoryAdapter() {
        menuCategoryList = new CopyOnWriteArrayList<>();
    }

    void setMenuCategoryList(List<MenuCategory> menuCategoryList) {
        this.menuCategoryList.clear();
        this.menuCategoryList.addAll(menuCategoryList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_menu_setting_menu_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        MenuCategory menuCategory = menuCategoryList.get(i);
        viewHolder.categoryName.setText(menuCategory.name);
    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_name) AppCompatTextView categoryName;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

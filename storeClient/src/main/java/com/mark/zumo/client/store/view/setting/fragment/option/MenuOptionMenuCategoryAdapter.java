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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionMenuCategoryAdapter extends RecyclerView.Adapter<MenuOptionMenuCategoryAdapter.ViewHolder> {

    private static final String TAG = "MenuOptionMenuCategoryAdapter";

    private List<MenuCategory> menuCategoryList;

    MenuOptionMenuCategoryAdapter() {
        menuCategoryList = new ArrayList<>();
    }

    void setMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        this.menuCategoryList = menuCategoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_optoin_setting_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        MenuCategory menuCategory = menuCategoryList.get(position);

        holder.name.setText(menuCategory.name);
        RecyclerView recyclerView = holder.recyclerView;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        MenuOptionMenuDetailAdapter menuOptionMenuDetailAdapter = new MenuOptionMenuDetailAdapter();
        recyclerView.setAdapter(menuOptionMenuDetailAdapter);
        menuOptionMenuDetailAdapter.setMenuList(menuCategory.menuList);
    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.recycler_view) RecyclerView recyclerView;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

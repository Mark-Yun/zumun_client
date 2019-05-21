/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.selector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.store.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 27.
 */
class MenuSelectorCategoryAdapter extends RecyclerView.Adapter<MenuSelectorCategoryAdapter.ViewHolder> {

    private final List<String> menuUuidListToExcept;
    private final List<String> menuUuidListToCheck;
    private final MenuCheckedListener listener;

    private List<MenuCategory> menuCategoryList;

    MenuSelectorCategoryAdapter(final List<String> menuUuidListToExcept,
                                final List<String> menuUuidListToCheck,
                                final MenuCheckedListener listener) {

        this.menuUuidListToExcept = menuUuidListToExcept;
        this.menuUuidListToCheck = menuUuidListToCheck;
        this.listener = listener;

        menuCategoryList = new ArrayList<>();
    }

    void setMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        this.menuCategoryList.clear();
        for (MenuCategory menuCategory : menuCategoryList) {

            List<Menu> tmpMenuList = new ArrayList<>();
            for (Menu menu : menuCategory.menuList) {
                if (menuUuidListToExcept.contains(menu.uuid)) {
                    continue;
                }
                tmpMenuList.add(menu);
            }

            menuCategory.menuList.clear();
            menuCategory.menuList.addAll(tmpMenuList);

            if (menuCategory.menuList == null || menuCategory.menuList.isEmpty()) {
                continue;
            }

            this.menuCategoryList.add(menuCategory);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_setting_selector_category, parent, false);
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

        MenuSelectorMenuAdapter menuSelectorMenuAdapter = new MenuSelectorMenuAdapter(menuUuidListToExcept, menuUuidListToCheck, listener);
        recyclerView.setAdapter(menuSelectorMenuAdapter);
        menuSelectorMenuAdapter.setMenuList(menuCategory.menuList);

        holder.name.setOnClickListener(v -> holder.checkBox.performClick());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> menuSelectorMenuAdapter.onClickParent(isChecked));
    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size();
    }

    interface MenuCheckedListener extends MenuSelectorMenuAdapter.OnMenuCheckedListener {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;
        @BindView(R.id.header) ConstraintLayout header;
        @BindView(R.id.recycler_view) RecyclerView recyclerView;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

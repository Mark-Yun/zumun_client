/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MenuViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 7.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<MenuCategory> categoryList;
    private Map<String, List<Menu>> menuListMap;

    private LifecycleOwner lifecycleOwner;
    private MenuViewModel menuViewModel;
    private String storeUuid;

    CategoryAdapter(final LifecycleOwner lifecycleOwner, final MenuViewModel menuViewModel, final String storeUuid) {
        this.lifecycleOwner = lifecycleOwner;
        this.menuViewModel = menuViewModel;
        this.storeUuid = storeUuid;

        categoryList = new ArrayList<>();
        menuListMap = new LinkedHashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_menu_category, parent, false);
        return new ViewHolder(view);
    }

    void setCategoryList(final List<MenuCategory> categoryList) {
        this.categoryList = categoryList;
        notifyIfReady();
    }

    void setMenuListMap(final Map<String, List<Menu>> menuListMap) {
        this.menuListMap = menuListMap;
        notifyIfReady();
    }

    private void notifyIfReady() {
        if (categoryList.size() < 1 || menuListMap.size() < 1) {
            return;
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        boolean isNoneCategory = (position == categoryList.size());

        MenuCategory menuCategory = isNoneCategory ? null : categoryList.get(position);
        String categoryName = isNoneCategory ? "None" : menuCategory.name;
        String categoryUuid = isNoneCategory ? "none" : menuCategory.uuid;

        holder.categoryName.setText(categoryName);

        RecyclerView recyclerView = holder.menuRecyclerView;
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        if (!isNoneCategory && !menuListMap.containsKey(categoryUuid)) {
            holder.categoryName.setVisibility(View.GONE);
            return;
        }

        List<Menu> menuList = menuListMap.get(categoryUuid);
        MenuAdapter menuAdapter = new MenuAdapter(lifecycleOwner, menuViewModel);
        recyclerView.setAdapter(menuAdapter);
        if (isNoneCategory) {
            menuViewModel.loadUnCategorizedMenu(storeUuid).observe(lifecycleOwner, menuAdapter::setMenuList);
        } else {
            menuAdapter.setMenuList(menuList);
        }
    }

    @Override
    public int getItemCount() {
        int size = categoryList.size();
        return size > 0 ? size + 1 : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_name) AppCompatTextView categoryName;
        @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

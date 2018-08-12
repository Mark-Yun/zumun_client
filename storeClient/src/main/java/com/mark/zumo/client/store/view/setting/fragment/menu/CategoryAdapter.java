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

package com.mark.zumo.client.store.view.setting.fragment.menu;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

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

    private final static int CATEGORY = 0;
    private final static int NONE_CATEGORY = 1;

    private FragmentManager fragmentManager;
    private MenuSettingViewModel menuSettingViewModel;
    private LifecycleOwner lifecycleOwner;

    private List<MenuCategory> categoryList;
    private Map<String, List<Menu>> menuListMap;

    CategoryAdapter(final LifecycleOwner lifecycleOwner,
                    final MenuSettingViewModel menuSettingViewModel,
                    final FragmentManager fragmentManager) {

        this.lifecycleOwner = lifecycleOwner;
        this.menuSettingViewModel = menuSettingViewModel;
        this.fragmentManager = fragmentManager;

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
        int viewType = getItemViewType(position);
        if (viewType == CATEGORY) {
            MenuCategory menuCategory = categoryList.get(position);
            String categoryName = menuCategory.name;
            String categoryUuid = menuCategory.uuid;

            holder.categoryName.setText(categoryName);

            boolean isEmptyCategory = !menuListMap.containsKey(categoryUuid);
            holder.categoryName.setVisibility(isEmptyCategory ? View.GONE : View.VISIBLE);
            MenuAdapter menuAdapter = getMenuAdapter(holder);
            List<Menu> menuList = isEmptyCategory ? new ArrayList<>() : menuListMap.get(categoryUuid);
            menuAdapter.setMenuList(menuList);
        } else if (viewType == NONE_CATEGORY) {
            String categoryName = "None";
            holder.categoryName.setText(categoryName);

            MenuAdapter menuAdapter = getMenuAdapter(holder);
            menuSettingViewModel.loadUnCategorizedMenu().observe(lifecycleOwner, menuAdapter::setMenuList);
        }
    }

    @NonNull
    private MenuAdapter getMenuAdapter(final @NonNull ViewHolder holder) {
        RecyclerView recyclerView = holder.menuRecyclerView;
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        MenuAdapter menuAdapter = new MenuAdapter(fragmentManager);
        recyclerView.setAdapter(menuAdapter);
        return menuAdapter;
    }

    @Override
    public int getItemViewType(final int position) {
        boolean isLastCategory = position == categoryList.size();
        return isLastCategory ? NONE_CATEGORY : CATEGORY;
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

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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.menulist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.view.util.RecyclerUtils;
import com.mark.zumo.client.store.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 7.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final MenuSelectListener menuSelectListener;

    final private List<MenuCategory> categoryList;
    final private Map<MenuCategory, MenuAdapter> menuAdapterMap;

    CategoryAdapter(MenuSelectListener menuSelectListener) {
        this.menuSelectListener = menuSelectListener;

        categoryList = new CopyOnWriteArrayList<>();
        menuAdapterMap = new HashMap<>();
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
        if (this.categoryList.equals(categoryList)) {
            return;
        }

        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();

        MenuCategory menuCategory = categoryList.get(position);
        String categoryName = menuCategory.name;
        boolean isEmptyCategory = menuCategory.menuList.isEmpty();

        holder.categoryName.setVisibility(isEmptyCategory ? View.GONE : View.VISIBLE);

        RecyclerView recyclerView = holder.recyclerView;
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);

        MenuAdapter menuAdapter = getMenuAdapter(menuCategory);
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setMenuList(menuCategory.menuList);
        if (!recyclerView.isAnimating()) {
            recyclerView.scheduleLayoutAnimation();
        }

        holder.categoryName.setText(categoryName + " (" + menuCategory.menuList.size() + ")");
        holder.categoryHeader.setOnClickListener(RecyclerUtils.recyclerViewExpandButton(recyclerView, holder.categoryHeader));
    }

    private MenuAdapter getMenuAdapter(final MenuCategory menuCategory) {
        if (menuAdapterMap.containsKey(menuCategory)) {
            return menuAdapterMap.get(menuCategory);
        }
        MenuAdapter menuAdapter = new MenuAdapter(menuSelectListener);
        menuAdapterMap.put(menuCategory, menuAdapter);
        return menuAdapter;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    interface MenuSelectListener extends MenuAdapter.MenuSelectListener {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_name) AppCompatTextView categoryName;
        @BindView(R.id.category_header) ConstraintLayout categoryHeader;
        @BindView(R.id.menu_recycler_view) RecyclerView recyclerView;
        @BindView(R.id.expand_button) AppCompatImageView expandButton;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

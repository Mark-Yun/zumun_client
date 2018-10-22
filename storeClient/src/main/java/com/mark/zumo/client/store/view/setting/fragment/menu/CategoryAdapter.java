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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 7.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private static final int ANIM_DURATION = 300;

    private final static int CATEGORY = 0;
    private final static int NONE_CATEGORY = 1;

    private FragmentManager fragmentManager;
    private MenuSettingViewModel menuSettingViewModel;
    private LifecycleOwner lifecycleOwner;

    private List<MenuCategory> categoryList;
    private List<MenuCategory> refinedCategoryList;
    private Map<String, List<Menu>> menuListMap;
    private Map<MenuCategory, MenuAdapter> menuAdapterMap;

    CategoryAdapter(final LifecycleOwner lifecycleOwner,
                    final MenuSettingViewModel menuSettingViewModel,
                    final FragmentManager fragmentManager) {

        this.lifecycleOwner = lifecycleOwner;
        this.menuSettingViewModel = menuSettingViewModel;
        this.fragmentManager = fragmentManager;

        categoryList = new ArrayList<>();
        refinedCategoryList = new ArrayList<>();
        menuListMap = new LinkedHashMap<>();
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

        this.categoryList = categoryList;
        notifyIfReady();
    }

    void setMenuListMap(final Map<String, List<Menu>> menuListMap) {
        if (this.menuListMap.equals(menuListMap)) {
            return;
        }

        this.menuListMap = menuListMap;
        notifyIfReady();
    }

    private void notifyIfReady() {
        if (categoryList.size() < 1 || menuListMap.size() < 1) {
            return;
        }
        List<MenuCategory> emptyList = new ArrayList<>();

        refinedCategoryList = new ArrayList<>(categoryList);
        for (MenuCategory category : refinedCategoryList) {
            boolean hasMenuItems = menuListMap.containsKey(category.uuid);
            if (!hasMenuItems) {
                emptyList.add(category);
            }
        }

        for (MenuCategory emptyCategory : emptyList) {
            refinedCategoryList.remove(emptyCategory);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        final Context context = holder.itemView.getContext();

        if (viewType == CATEGORY) {
            MenuCategory menuCategory = refinedCategoryList.get(position);
            String categoryName = menuCategory.name;
            String categoryUuid = menuCategory.uuid;
            boolean isEmptyCategory = !menuListMap.containsKey(categoryUuid);
            List<Menu> menuList = isEmptyCategory ? new ArrayList<>() : menuListMap.get(categoryUuid);

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
            menuAdapter.setMenuList(menuList);
            if (!recyclerView.isAnimating()) {
                recyclerView.scheduleLayoutAnimation();
            }

            holder.categoryName.setText(categoryName + " (" + menuList.size() + ")");
            holder.categoryHeader.setOnClickListener(new View.OnClickListener() {
                private boolean isVisible = true;
                private int viewHeight;

                @Override
                public void onClick(final View v) {
                    v.setClickable(false);
                    holder.expandButton.animate()
                            .setDuration(ANIM_DURATION)
                            .rotation(isVisible ? 180 : 0);

                    ValueAnimator anim = isVisible
                            ? ValueAnimator.ofInt(viewHeight = recyclerView.getMeasuredHeight(), 0)
                            : ValueAnimator.ofInt(0, viewHeight);

                    anim.addUpdateListener(valueAnimator -> {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                        layoutParams.height = val;
                        recyclerView.setLayoutParams(layoutParams);
                    });
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            super.onAnimationEnd(animation);
                            isVisible = !isVisible;
                            v.setClickable(true);
                        }
                    });
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.setDuration(ANIM_DURATION);
                    anim.start();
                }
            });

        } else if (viewType == NONE_CATEGORY) {
            holder.categoryName.setText("None");

            RecyclerView recyclerView = holder.recyclerView;
            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);

            MenuAdapter menuAdapter = new MenuAdapter(fragmentManager);
            recyclerView.setAdapter(menuAdapter);
            menuSettingViewModel.loadUnCategorizedMenu().observe(lifecycleOwner, menuAdapter::setMenuList);
        }
    }

    private MenuAdapter getMenuAdapter(final MenuCategory menuCategory) {
        if (menuAdapterMap.containsKey(menuCategory)) {
            return menuAdapterMap.get(menuCategory);
        }
        MenuAdapter menuAdapter = new MenuAdapter(fragmentManager);
        menuAdapterMap.put(menuCategory, menuAdapter);
        return menuAdapter;
    }

    @Override
    public int getItemViewType(final int position) {
        boolean isLastCategory = position == refinedCategoryList.size();
        return isLastCategory ? NONE_CATEGORY : CATEGORY;
    }

    @Override
    public int getItemCount() {
        int size = refinedCategoryList.size();
        return size > 0 ? size + 1 : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_name) AppCompatTextView categoryName;
        @BindView(R.id.category_header) ConstraintLayout categoryHeader;
        @BindView(R.id.menu_recycler_view) RecyclerView recyclerView;
        @BindView(R.id.category_expand_button) AppCompatImageView expandButton;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

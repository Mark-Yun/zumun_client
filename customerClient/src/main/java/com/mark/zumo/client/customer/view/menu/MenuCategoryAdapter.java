/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 7.
 */
public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder> {

    private static final int ANIM_DURATION = 300;

    private final List<MenuCategory> categoryList;
    private final LifecycleOwner lifecycleOwner;
    private final MenuAdapter.MenuSelectListener listener;

    MenuCategoryAdapter(final LifecycleOwner lifecycleOwner, final MenuAdapter.MenuSelectListener listener) {

        this.lifecycleOwner = lifecycleOwner;
        this.listener = listener;

        categoryList = new ArrayList<>();
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
        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuCategory menuCategory = categoryList.get(position);
        String categoryName = menuCategory.name;


        RecyclerView recyclerView = holder.menuRecyclerView;
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        Context context = holder.itemView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);

        MenuAdapter menuAdapter = new MenuAdapter(lifecycleOwner, listener);
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setMenuList(menuCategory.menuList);
        recyclerView.scheduleLayoutAnimation();

        holder.categoryName.setText(categoryName + " (" + menuCategory.menuList.size() + ")");

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
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_name) AppCompatTextView categoryName;
        @BindView(R.id.category_header) ConstraintLayout categoryHeader;
        @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;
        @BindView(R.id.category_expand_button) AppCompatImageView expandButton;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

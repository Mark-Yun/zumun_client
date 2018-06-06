/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.view.acceptedorder.widget;

/**
 * Created by mark on 18. 5. 16.
 */

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.LoopRecyclerViewPager;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager.OnPageChangedListener;
import com.lsjwzh.widget.recyclerviewpager.ViewUtils;

import java.lang.ref.WeakReference;

public class TabLayoutSupport {

    public static void setupWithViewPager(@NonNull TabLayout tabLayout,
                                          @NonNull RecyclerViewPager viewPager,
                                          @NonNull TabLayoutSupport.ViewPagerTabLayoutAdapter viewPagerTabLayoutAdapter) {
        tabLayout.removeAllTabs();
        int i = 0;

        for (int count = viewPagerTabLayoutAdapter.getItemCount(); i < count; ++i) {
            ViewGroup rootView = (ViewGroup) tabLayout.getRootView();
            RecyclerView.ViewHolder viewHolder = viewPagerTabLayoutAdapter.createTabView(rootView);
            viewPagerTabLayoutAdapter.bindTabView(viewHolder, i);
            tabLayout.addTab(tabLayout.newTab().setCustomView(viewHolder.itemView));
        }

        TabLayoutSupport.TabLayoutOnPageChangeListener listener = new TabLayoutSupport.TabLayoutOnPageChangeListener(tabLayout, viewPager);
        viewPager.addOnScrollListener(listener);
        viewPager.addOnPageChangedListener(listener);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(160, 0, 160, 0);
        tabLayout.addOnTabSelectedListener(new TabLayoutSupport.ViewPagerOnTabSelectedListener(viewPager));
    }

    public interface ViewPagerTabLayoutAdapter<T extends RecyclerView.ViewHolder> {
        T createTabView(@NonNull final ViewGroup parent);
        void bindTabView(@NonNull final T holder, final int position);
        int getItemCount();
    }

    public static class TabLayoutOnPageChangeListener extends OnScrollListener implements OnPageChangedListener {
        private final WeakReference<TabLayout> mTabLayoutRef;
        private final WeakReference<RecyclerViewPager> mViewPagerRef;
        private int mPositionBeforeScroll;
        private int mPagerLeftBeforeScroll;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout, RecyclerViewPager viewPager) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
            this.mViewPagerRef = new WeakReference<>(viewPager);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0) {
                this.mPositionBeforeScroll = -1;
                this.mPagerLeftBeforeScroll = 0;
            } else if (this.mPositionBeforeScroll < 0) {
                this.mPositionBeforeScroll = ((RecyclerViewPager) recyclerView).getCurrentPosition();
                this.mPagerLeftBeforeScroll = recyclerView.getPaddingLeft();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView == null) {
                return;
            }

            RecyclerViewPager viewPager = (RecyclerViewPager) recyclerView;
            int pagerWidth = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
            View centerXChild = ViewUtils.getCenterXChild(viewPager);
            if (centerXChild == null) {
                return;
            }

            int centerChildPosition = viewPager.getChildAdapterPosition(centerXChild);
            float offset = (float) (this.mPagerLeftBeforeScroll - centerXChild.getLeft() + pagerWidth * (centerChildPosition - this.mPositionBeforeScroll));
            float positionOffset = offset * 1.0F / (float) pagerWidth;

            TabLayout tabLayout = this.mTabLayoutRef.get();
            if (tabLayout == null) {
                return;
            }

            try {
                if (positionOffset < 0.0F) {
                    int position = this.mPositionBeforeScroll + (int) Math.floor((double) positionOffset);
                    tabLayout.setScrollPosition(position, positionOffset - (float) ((int) Math.floor((double) positionOffset)), true);
                } else {
                    int position = this.mPositionBeforeScroll + (int) positionOffset;
                    tabLayout.setScrollPosition(position, positionOffset - (float) ((int) positionOffset), true);
                }
            } catch (Exception ignored) {
            }
        }

        @Override
        public void OnPageChanged(int oldPosition, int newPosition) {
            if (this.mViewPagerRef.get() != null) {
                if (this.mViewPagerRef.get() instanceof LoopRecyclerViewPager) {
                    newPosition = ((LoopRecyclerViewPager) this.mViewPagerRef.get()).transformToActualPosition(newPosition);
                }

                TabLayout tabLayout = this.mTabLayoutRef.get();
                if (tabLayout != null && tabLayout.getTabAt(newPosition) != null) {
                    tabLayout.getTabAt(newPosition).select();
                }

            }
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final RecyclerViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(RecyclerViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        public void onTabSelected(Tab tab) {
            this.mViewPager.smoothScrollToPosition(tab.getPosition());
        }

        public void onTabUnselected(Tab tab) {
        }

        public void onTabReselected(Tab tab) {
        }
    }
}

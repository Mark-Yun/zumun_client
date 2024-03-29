/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MainViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 3.
 */
public class FindingStoreFragment extends Fragment {
    public static final String TAG = "FindingStoreFragment";
    @BindView(R.id.finding_glass) AppCompatImageView findingGlass;
    @BindView(R.id.store_image) AppCompatImageView storeImage;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private StoreFindListener storeFindListener;
    private MainViewModel mainViewModel;

    void setStoreFindListener(final StoreFindListener storeFindListener) {
        this.storeFindListener = storeFindListener;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finding_store, container, false);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        mainViewModel.findStore(getActivity()).observe(this, this::onD2dLoadStore);
        return view;
    }

    private void onD2dLoadStore(Store store) {
        Log.d(TAG, "onD2dLoadStore: ");
        storeFindListener.onStoreFound(store);
    }

    private void onRefresh() {
        new Handler().postDelayed(() -> {
            mainViewModel.findStore(getActivity()).observe(this, this::onD2dLoadStore);
            swipeRefreshLayout.setRefreshing(false);
            startFindingAnimation();
        }, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        startFindingAnimation();
    }

    private void startFindingAnimation() {
        if (!isAdded()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int left = (int) (size.x * 0.25);
            int top = (int) (size.y * 0.25);
            int right = (int) (size.x * 0.55);
            int bottom = (int) (size.y * 0.45);

            Path path = new Path();
            path.arcTo(left, top, right, bottom, 270, 359.99f, true);
            ObjectAnimator animator = ObjectAnimator.ofFloat(findingGlass, View.X, View.Y, path);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(3000);
            animator.start();

            AnimationDrawable animDrawable = (AnimationDrawable) storeImage.getDrawable();
            animDrawable.setEnterFadeDuration(500);
            animDrawable.setExitFadeDuration(500);
            animDrawable.start();
        }
    }

    interface StoreFindListener {
        void onStoreFound(final Store store);
    }
}

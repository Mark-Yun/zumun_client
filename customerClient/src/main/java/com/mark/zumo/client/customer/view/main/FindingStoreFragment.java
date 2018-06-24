/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 3.
 */
public class FindingStoreFragment extends Fragment {
    @BindView(R.id.finding_glass) AppCompatImageView findingGlass;
    @BindView(R.id.store_image) AppCompatImageView storeImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finding_store, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startFindingAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void startFindingAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
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
}

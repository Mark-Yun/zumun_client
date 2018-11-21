/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.view.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mark on 18. 11. 21.
 */
public final class RecyclerUtils {
    private static final int ANIM_DURATION = 300;

    public static View.OnClickListener recyclerViewExpandButton(@NonNull final RecyclerView recyclerView,
                                                                @NonNull final View expandButton) {
        return new View.OnClickListener() {
            private boolean isVisible = recyclerView.getVisibility() == View.VISIBLE;
            private int viewHeight;

            @Override
            public void onClick(final View v) {
                if (!isVisible && viewHeight == 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    isVisible = !isVisible;
                    return;
                }

                v.setClickable(false);
                expandButton.animate()
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
        };
    }
}

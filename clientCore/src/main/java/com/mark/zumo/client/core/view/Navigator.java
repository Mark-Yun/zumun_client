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

package com.mark.zumo.client.core.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.wonderkiln.blurkit.BlurLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mark on 18. 5. 25.
 */
public final class Navigator {

    private static List<BlurLayout> blurLayoutStack = new ArrayList<>();

    public static void addBlurFilter(final BlurLayout blurLayout) {
        blurLayoutStack.add(blurLayout);
    }

    public static void removeBlurFilter(final BlurLayout blurLayout) {
        blurLayoutStack.remove(blurLayout);
    }

    public static void setBlurLayoutVisible(boolean visible) {
        final BlurLayout blurLayout = getTopBlurLayout();
        if (blurLayout == null) return;
        if ((blurLayout.getVisibility() == View.VISIBLE) == visible) return;

        int animRes = visible ? android.R.anim.fade_in : android.R.anim.fade_out;
        Animation animationFade = AnimationUtils.loadAnimation(ContextHolder.getContext(), animRes);
        animationFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
                blurLayout.setAlpha(visible ? 0 : 1f);
                blurLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                if (!visible) blurLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        blurLayout.startAnimation(animationFade);
        blurLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private static BlurLayout getTopBlurLayout() {
        return blurLayoutStack.get(blurLayoutStack.size() - 1);
    }
}

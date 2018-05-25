package com.mark.zumo.client.customer.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.wonderkiln.blurkit.BlurLayout;


/**
 * Created by mark on 18. 5. 25.
 */
public final class Navigator {

    private static BlurLayout blurLayout;

    public static void setBlurFilter(final BlurLayout blurLayout) {
        Navigator.blurLayout = blurLayout;
    }

    public static void setBlurLayoutVisible(boolean visible) {
        if (blurLayout == null) return;

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
}

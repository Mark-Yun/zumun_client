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

package com.mark.zumo.client.store.view.sign.user;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.transformation.StoreSignUpFragmentBackground;
import com.mark.zumo.client.core.view.BaseActivity;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.main.MainActivity;
import com.mark.zumo.client.store.view.sign.user.fragment.BackPressedInterceptor;
import com.mark.zumo.client.store.view.sign.user.fragment.UserSignMainFragment;
import com.mark.zumo.client.store.viewmodel.StoreUserSignViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 7.
 */

public class UserSignUpActivity extends BaseActivity {

    @BindView(R.id.description_title) AppCompatTextView descriptionTitle;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;
    @BindView(R.id.console_fragment) ConstraintLayout consoleFragment;
    @BindView(R.id.activity_layout) ConstraintLayout activityLayout;
    @BindView(R.id.background_image_view) AppCompatImageView backgroundImageView;

    private StoreUserSignViewModel storeUserSignViewModel;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, UserSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.icon_anim_fade_in, R.anim.icon_anim_fade_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeUserSignViewModel = ViewModelProviders.of(this).get(StoreUserSignViewModel.class);
        if (storeUserSignViewModel.hasStoreUserSessionSync()) {
            MainActivity.start(this);
            return;
        }

        storeUserSignViewModel.hasStoreUserSessionAsync().observe(this, this::onSessionLoaded);

        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        inflateView();
    }

    private void inflateView() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.console_fragment, Fragment.instantiate(this, UserSignMainFragment.class.getName()))
                .commit();

        GlideApp.with(this)
                .load(R.drawable.background_image_sign_up_coffeehouse)
                .apply(RequestOptions.centerCropTransform())
                .transform(new StoreSignUpFragmentBackground())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backgroundImageView);

        Animation animationFade = AnimationUtils.loadAnimation(ContextHolder.getContext(), android.R.anim.fade_in);
        animationFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
                descriptionTitle.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                descriptionTitle.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        descriptionTitle.startAnimation(animationFade);
    }

    private boolean fragmentsBackKeyIntercept() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BackPressedInterceptor) {
                ((BackPressedInterceptor) fragment).onClickedBackButton();
                return true;
            }
        }
        return false;
    }

    private void onSessionLoaded(boolean hasSession) {
        if (hasSession) {
            MainActivity.start(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentsBackKeyIntercept()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

}

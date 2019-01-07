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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.transformation.StoreSignUpFragmentBackground;
import com.mark.zumo.client.core.view.BaseActivity;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.main.MainActivity;
import com.mark.zumo.client.store.view.main.fragment.storeselect.StoreSelectFragment;
import com.mark.zumo.client.store.view.sign.user.fragment.BackPressedInterceptor;
import com.mark.zumo.client.store.view.sign.user.fragment.SignFragment;
import com.mark.zumo.client.store.viewmodel.StoreUserSignViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 7.
 */

public class UserSignActivity extends BaseActivity {

    @BindView(R.id.background_image_view) AppCompatImageView backgroundImageView;

    private StoreUserSignViewModel storeUserSignViewModel;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, UserSignActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.icon_anim_fade_in, R.anim.icon_anim_fade_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeUserSignViewModel = ViewModelProviders.of(this).get(StoreUserSignViewModel.class);
        if (storeUserSignViewModel.hasStoreUserSessionSync()) {
            if (storeUserSignViewModel.hasSessionStoreSync()) {
                MainActivity.start(this);
                return;
            } else {
                storeUserSignViewModel.hasSessionStoreAsync().observe(this, this::onSessionStoreLoaded);
            }
        } else {
            storeUserSignViewModel.hasStoreUserSessionAsync().observe(this, this::onStoreUserSessionLoaded);
        }

        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);

        inflateBackgroundImage();
    }

    private void inflateBackgroundImage() {
        GlideApp.with(this)
                .load(R.drawable.background_image_sign_up_coffeehouse)
                .apply(RequestOptions.centerCropTransform())
                .transform(new StoreSignUpFragmentBackground())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backgroundImageView);
    }


    private void onStoreUserSessionLoaded(boolean hasSession) {
        if (!hasSession) {
            inflateSignUpFragment();
            return;
        }

        if (storeUserSignViewModel.hasSessionStoreSync()) {
            MainActivity.start(this);
        } else {
            storeUserSignViewModel.hasSessionStoreAsync().observe(this, this::onSessionStoreLoaded);
        }
    }

    private void onSessionStoreLoaded(boolean hasSessionStore) {
        if (!hasSessionStore) {
            inflateStoreSelectFragment();
        } else {
            MainActivity.start(this);
        }
    }

    private void inflateStoreSelectFragment() {
        Fragment fragment = StoreSelectFragment.newInstance()
                .onSelectStore(store -> onSessionStoreLoaded(true));

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_fragment, fragment)
                .commit();
    }

    private void inflateSignUpFragment() {
        SignFragment signFragment = SignFragment.newInstance()
                .doOnSuccessSignIn(() -> onStoreUserSessionLoaded(true));

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_fragment, signFragment)
                .commit();
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

    @Override
    public void onBackPressed() {
        if (fragmentsBackKeyIntercept()) {
            return;
        }
        super.onBackPressed();
    }
}

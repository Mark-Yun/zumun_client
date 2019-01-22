/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.store.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 1. 7.
 */
public class SignFragment extends Fragment {

    @BindView(R.id.description_title) AppCompatTextView descriptionTitle;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;
    @BindView(R.id.console_fragment) ConstraintLayout consoleFragment;
    @BindView(R.id.activity_layout) ConstraintLayout activityLayout;

    private Runnable signInSuccessAction;

    public static SignFragment newInstance() {

        Bundle args = new Bundle();

        SignFragment fragment = new SignFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        ButterKnife.bind(this, view);

        inflateView();

        return view;
    }

    public SignFragment doOnSuccessSignIn(Runnable signInSuccessAction) {
        this.signInSuccessAction = signInSuccessAction;
        return this;
    }

    private void inflateView() {
        UserSignMainFragment userSignMainFragment = UserSignMainFragment.newInstance()
                .doOnSuccessSignIn(signInSuccessAction);

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.console_fragment, userSignMainFragment)
                .commit();

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
}

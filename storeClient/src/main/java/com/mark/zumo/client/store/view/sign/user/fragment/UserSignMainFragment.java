/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 16.
 */
public class UserSignMainFragment extends Fragment {

    private UserSignInFragment signInFragment;
    private UserSignUpFragment signUpFragment;
    private FindEmailFragment findEmailFragment;
    private FindPasswordFragment findPasswordFragment;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_user_sign_main, container, false);
        ButterKnife.bind(this, view);

        inflateFragment();

        return view;
    }

    private void inflateFragment() {
        if (signInFragment == null) {
            signInFragment = createSignInFragment();
        }
        updateConsoleFragment(signInFragment);
    }

    private void onFindMyEmail() {
        if (findEmailFragment == null) {
            findEmailFragment = createFindMyEmailFragment();
        }
        updateConsoleFragment(findEmailFragment);
    }

    private void onFindMyPassword() {
        if (findPasswordFragment == null) {
            findPasswordFragment = createFindMyPasswordFragment();
        }
        updateConsoleFragment(findPasswordFragment);
    }

    private void onSignUp() {
        if (signUpFragment == null) {
            signUpFragment = createSignUpFragment();
        }
        updateConsoleFragment(signUpFragment);
    }

    private void onBackPressured() {
        if (signInFragment == null) {
            signInFragment = createSignInFragment();
        }

        updateConsoleFragment(signInFragment);
    }

    private UserSignUpFragment createSignUpFragment() {
        UserSignUpFragment userSignUpFragment = (UserSignUpFragment) Fragment.instantiate(getActivity(), UserSignUpFragment.class.getName());
        return userSignUpFragment.doOnBackPressured(this::onBackPressured);
    }

    private UserSignInFragment createSignInFragment() {
        UserSignInFragment userSignInFragment = (UserSignInFragment) Fragment.instantiate(getContext(), UserSignInFragment.class.getName());
        return userSignInFragment.doOnFindMyEmail(this::onFindMyEmail)
                .doOnFindMyPassword(this::onFindMyPassword)
                .doOnSignup(this::onSignUp);
    }

    private FindEmailFragment createFindMyEmailFragment() {
        FindEmailFragment findEmailFragment = (FindEmailFragment) Fragment.instantiate(getContext(), FindEmailFragment.class.getName());
        return findEmailFragment.doOnBackPressured(this::onBackPressured);
    }

    private FindPasswordFragment createFindMyPasswordFragment() {
        FindPasswordFragment findPasswordFragment = (FindPasswordFragment) Fragment.instantiate(getContext(), FindPasswordFragment.class.getName());
        return findPasswordFragment.doOnBackPressured(this::onBackPressured);
    }

    private void updateConsoleFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.sign_console, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}

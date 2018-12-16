/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mark.zumo.client.store.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 13.
 */
public class UserSignInFragment extends Fragment {

    @BindView(R.id.logo) AppCompatImageView logo;
    @BindView(R.id.input_email) TextInputEditText inputEmail;
    @BindView(R.id.input_password) TextInputEditText inputPassword;
    @BindView(R.id.button_sign_in) AppCompatButton buttonSignIn;
    @BindView(R.id.auto_login) SwitchCompat autoLogin;
    @BindView(R.id.find_my_email) AppCompatTextView findMyEmail;
    @BindView(R.id.find_my_password) AppCompatTextView findMyPassword;
    @BindView(R.id.sign_up) AppCompatTextView signUp;

    private Runnable findMyEmailAction;
    private Runnable findMyPasswordAction;
    private Runnable signUpAction;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_user_sign_in, container, false);
        ButterKnife.bind(this, view);

        autoLogin.setOnCheckedChangeListener(this::onAutoLoginChanged);
        return view;
    }

    private void onAutoLoginChanged(CompoundButton view, boolean isChecked) {

    }

    @OnClick(R.id.button_sign_in)
    void onClickSignIn() {

    }

    @OnClick(R.id.sign_up)
    void onClickSignUp() {
        signUpAction.run();
    }

    @OnClick(R.id.find_my_email)
    void onClickFindMyEmail() {
        findMyEmailAction.run();
    }

    @OnClick(R.id.find_my_password)
    void onClickFindMyPassword() {
        findMyEmailAction.run();
    }

    public UserSignInFragment doOnFindMyEmail(Runnable runnable) {
        findMyEmailAction = runnable;
        return this;
    }

    public UserSignInFragment doOnFindMyPassword(Runnable runnable) {
        findMyPasswordAction = runnable;
        return this;
    }

    public UserSignInFragment doOnSignup(Runnable runnable) {
        signUpAction = runnable;
        return this;
    }
}

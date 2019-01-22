/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInErrorCode;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.StoreUserSignViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInErrorCode.FAIL;

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
    private Runnable startLoadingAction;
    private Runnable stopLoadingAction;
    private Runnable signInSuccessAction;

    private StoreUserSignViewModel storeUserSignViewModel;

    public static UserSignInFragment newInstance() {

        Bundle args = new Bundle();

        UserSignInFragment fragment = new UserSignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeUserSignViewModel = ViewModelProviders.of(this).get(StoreUserSignViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_user_sign_in, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_sign_in)
    void onClickSignIn() {
        boolean isAutoLogin = autoLogin.isChecked();
        String email = inputEmail.getText() != null ? inputEmail.getText().toString() : "";
        String password = inputPassword.getText() != null ? inputPassword.getText().toString() : "";

        if (checkErrorData(email, password)) {
            return;
        }

        storeUserSignViewModel.loginStoreUser(email, password, isAutoLogin).observe(this, this::onLoginResult);

        autoLogin.clearFocus();
        inputEmail.clearFocus();
        inputPassword.clearFocus();
        buttonSignIn.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        startLoadingAction.run();
    }

    private boolean checkErrorData(final String email, final String password) {
        inputEmail.setError(null);
        inputPassword.setError(null);

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.sign_up_error_message_empty_email));
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.sign_up_error_message_empty_password));
            return true;
        }

        return false;
    }

    private void onLoginResult(StoreUserSignInErrorCode storeUserSignInErrorCode) {
        stopLoadingAction.run();

        switch (storeUserSignInErrorCode) {
            case OK:
                signInSuccessAction.run();
                break;
            case FAIL:
                new AlertDialog.Builder(getActivity())
                        .setMessage(FAIL.message)
                        .setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()))
                        .setCancelable(true)
                        .create()
                        .show();
                break;
        }
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
        findMyPasswordAction.run();
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

    public UserSignInFragment doOnStartLoading(final Runnable startLoadingAction) {
        this.startLoadingAction = startLoadingAction;
        return this;
    }

    public UserSignInFragment doOnStopLoading(final Runnable stopLoadingAction) {
        this.stopLoadingAction = stopLoadingAction;
        return this;
    }

    public UserSignInFragment doOnSuccessSignIn(final Runnable signInSuccessAction) {
        this.signInSuccessAction = signInSuccessAction;
        return this;
    }
}

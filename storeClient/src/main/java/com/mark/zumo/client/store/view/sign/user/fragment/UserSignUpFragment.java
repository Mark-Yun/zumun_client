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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.StoreUserSignViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 13.
 */
public class UserSignUpFragment extends Fragment implements BackPressedInterceptor {

    private static final String TAG = "UserSignUpFragment";

    @BindView(R.id.input_layout_email) TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_password) TextInputLayout inputLayoutPassword;
    @BindView(R.id.input_layout_confirm_password) TextInputLayout inputLayoutConfirmPassword;

    @BindView(R.id.button_sign_up) AppCompatButton buttonSignUp;

    @BindView(R.id.input_email) TextInputEditText inputEmail;
    @BindView(R.id.input_password) TextInputEditText inputPassword;
    @BindView(R.id.input_password_confirm) TextInputEditText inputPasswordConfirm;
    @BindView(R.id.input_phone_number) TextInputEditText inputPhoneNumber;

    private StoreUserSignViewModel storeUserSignViewModel;

    private Runnable onBackPressedAction;
    private Runnable startLoadingAction;
    private Runnable stopLoadingAction;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeUserSignViewModel = ViewModelProviders.of(this).get(StoreUserSignViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_user_sign_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_sign_up)
    void onClickSignUp() {

        inputEmail.clearFocus();
        inputPassword.clearFocus();
        inputPasswordConfirm.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        startLoadingAction.run();
        String email = inputLayoutEmail.getEditText() == null ? "" : inputLayoutEmail.getEditText().getText().toString();
        String password = inputLayoutPassword.getEditText() == null ? "" : inputLayoutPassword.getEditText().getText().toString();
        String passwordConfirm = inputLayoutConfirmPassword.getEditText() == null ? "" : inputLayoutConfirmPassword.getEditText().getText().toString();
        String phoneNumber = inputPhoneNumber.getEditableText() == null ? "" : inputPhoneNumber.getEditableText().toString();
        storeUserSignViewModel.signUp(email, password, passwordConfirm, phoneNumber)
                .observe(this, this::onSignUpRequest);
    }

    private void onSignUpRequest(StoreUserSignupException e) {
        Log.e(TAG, "onSignUpRequest: " + e.message);

        stopLoadingAction.run();

        StoreUserSignupErrorCode storeUserSignupErrorCode = e.storeUserSignupErrorCode;
        String message = e.message;
        TextInputLayout targetInputLayout = null;
        switch (storeUserSignupErrorCode) {
            case SUCCESS:
                onSuccessSignUp();
                break;
            case EMPTY_EMAIL:
                targetInputLayout = inputLayoutEmail;
                break;
            case EMAIL_INCORRECT:
                targetInputLayout = inputLayoutEmail;
                break;
            case EMPTY_PASSWORD:
                targetInputLayout = inputLayoutPassword;
                break;
            case EMPTY_PASSWORD_CONFIRM:
                targetInputLayout = inputLayoutConfirmPassword;
                break;
            case PASSWORD_DISCORD:
                targetInputLayout = inputLayoutConfirmPassword;
                break;
            default:
                Log.e(TAG, "onSignUpRequest: exception occurred!", e);
                return;
        }
        clearError();

        if (targetInputLayout != null) {
            targetInputLayout.setError(message);
        }
    }

    private void onSuccessSignUp() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sign_up_success_dialog_title)
                .setMessage(R.string.sign_up_success_dialog_message)
                .setOnDismissListener(dialog -> onDismissDialog())
                .create()
                .show();
    }

    private void onDismissDialog() {
        inputEmail.setText("");
        inputPassword.setText("");
        inputPasswordConfirm.setText("");
        onBackPressedAction.run();
    }

    @Override
    @OnClick(R.id.back)
    public void onClickedBackButton() {
        onBackPressedAction.run();
    }

    private void clearError() {
        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        inputLayoutConfirmPassword.setError(null);
    }

    public UserSignUpFragment doOnBackPressured(Runnable runnable) {
        onBackPressedAction = runnable;
        return this;
    }

    public UserSignUpFragment doOnStartLoading(final Runnable startLoadingAction) {
        this.startLoadingAction = startLoadingAction;
        return this;
    }

    public UserSignUpFragment doOnStopLoading(final Runnable stopLoadingAction) {
        this.stopLoadingAction = stopLoadingAction;
        return this;
    }
}
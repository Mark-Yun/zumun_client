/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupErrorCode;
import com.mark.zumo.client.core.appserver.request.signup.StoreUserSignupException;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.util.ImagePickerUtils;
import com.mark.zumo.client.store.viewmodel.SignUpViewModel;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 13.
 */
public class UserSignUpFragment extends Fragment implements BackPressedInterceptor {

    private static final String TAG = "UserSignUpFragment";
    private static final int BANK_SCAN_IMAGE_PICKER_REQUEST_CODE = 22;

    @BindView(R.id.input_layout_email) TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_password) TextInputLayout inputLayoutPassword;
    @BindView(R.id.input_layout_confirm_password) TextInputLayout inputLayoutConfirmPassword;
    @BindView(R.id.input_layout_name) TextInputLayout inputLayoutName;
    @BindView(R.id.input_layout_phone_number) TextInputLayout inputLayoutPhoneNumber;
    @BindView(R.id.input_bank_name) AppCompatSpinner inputBankName;
    @BindView(R.id.input_layout_bank) TextInputLayout inputLayoutBank;

    @BindView(R.id.button_sign_up) AppCompatButton buttonSignUp;

    @BindView(R.id.bank_account_scan_image) AppCompatImageView bankAccountScanImage;
    @BindView(R.id.bank_account_scan_image_description) ConstraintLayout bankAccountScanImageDescription;

    private SignUpViewModel signUpViewModel;
    private String bankAccountScanUrl;
    private Runnable onBackPressedAction;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
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
        String email = inputLayoutEmail.getEditText() == null ? "" : inputLayoutEmail.getEditText().getText().toString();
        String password = inputLayoutPassword.getEditText() == null ? "" : inputLayoutPassword.getEditText().getText().toString();
        String passwordConfirm = inputLayoutConfirmPassword.getEditText() == null ? "" : inputLayoutConfirmPassword.getEditText().getText().toString();
        String name = inputLayoutName.getEditText() == null ? "" : inputLayoutName.getEditText().getText().toString();
        String phoneNumber = inputLayoutPhoneNumber.getEditText() == null ? "" : inputLayoutPhoneNumber.getEditText().getText().toString();
        String bankName = inputBankName.getSelectedItem() == null ? "" : inputBankName.getSelectedItem().toString();
        String bankAccount = inputLayoutBank.getEditText() == null ? "" : inputLayoutBank.getEditText().getText().toString();
        signUpViewModel.signUp(email, password, passwordConfirm, name, phoneNumber, bankName, bankAccount, bankAccountScanUrl)
                .observe(this, this::onSignUpRequest);
    }

    private void onSignUpRequest(StoreUserSignupException e) {
        Log.e(TAG, "onSignUpRequest: " + e.message);

        StoreUserSignupErrorCode storeUserSignupErrorCode = e.storeUserSignupErrorCode;
        String message = e.message;
        TextInputLayout targetInputLayout = null;
        switch (storeUserSignupErrorCode) {
            case SUCCESS:
                break;
            case EMPTY_EMAIL:
                targetInputLayout = inputLayoutEmail;
                break;
            case EMAIL_INCORRECT:
                targetInputLayout = inputLayoutEmail;
                break;
            case EMPTY_BANK_ACCOUNT:
                targetInputLayout = inputLayoutBank;
                break;
            case EMPTY_BANK_ACCOUNT_URL:
                targetInputLayout = inputLayoutBank;
                break;
            case EMPTY_BANK_NAME:
                targetInputLayout = inputLayoutBank;
                break;
            case EMPTY_NAME:
                targetInputLayout = inputLayoutName;
                break;
            case EMPTY_PASSWORD:
                targetInputLayout = inputLayoutPassword;
                break;
            case EMPTY_PASSWORD_CONFIRM:
                targetInputLayout = inputLayoutConfirmPassword;
                break;
            case EMPTY_PHONE_NUMBER:
                targetInputLayout = inputLayoutPhoneNumber;
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

    @Override
    @OnClick(R.id.back)
    public void onClickedBackButton() {
        onBackPressedAction.run();
    }

    @OnClick(R.id.bank_account_scan_image)
    void onClickBackAccountScanImage() {
        ImagePickerUtils.showImagePickerStoreThumbnail(getActivity(), BANK_SCAN_IMAGE_PICKER_REQUEST_CODE);
    }

    private void clearError() {
        inputLayoutEmail.setError(null);
        inputLayoutPassword.setError(null);
        inputLayoutConfirmPassword.setError(null);
        inputLayoutName.setError(null);
        inputLayoutPhoneNumber.setError(null);
        inputLayoutBank.setError(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != AppCompatActivity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case BANK_SCAN_IMAGE_PICKER_REQUEST_CODE:
                onBankAccountScanImagePicked(data);
                break;
        }
    }

    private void onBankAccountScanImagePicked(Intent data) {

        Serializable serializableExtra = data.getSerializableExtra(GalleryActivity.PHOTOS);
        List<String> photoList = (List<String>) serializableExtra;
        if (photoList == null || photoList.size() == 0) {
            return;
        }

        Uri selectedPath = Uri.parse(photoList.get(0));
        signUpViewModel.uploadBankAccountScanImage(getActivity(), selectedPath).observe(this, this::onUploadBankAccountScanImage);
    }

    private void onUploadBankAccountScanImage(String url) {
        GlideApp.with(getActivity())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bankAccountScanImage);

        bankAccountScanUrl = url;

        bankAccountScanImageDescription.setVisibility(View.GONE);
    }

    public UserSignUpFragment doOnBackPressured(Runnable runnable) {
        onBackPressedAction = runnable;
        return this;
    }
}

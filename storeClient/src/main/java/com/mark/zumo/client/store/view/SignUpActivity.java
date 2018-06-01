/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.SignUpViewModel;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpActivity extends BaseActivity {
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        setContentView(R.layout.activity_sign_up);
    }
}

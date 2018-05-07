package com.mark.zumo.client.store.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.SignUpViewModel;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        setContentView(R.layout.activity_sign_up);
    }
}

package com.mark.zumo.client.customer.view.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.signup.fragment.SignUpButtonFragment;
import com.mark.zumo.client.customer.viewmodel.SignUpViewModel;

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

        inflateButtonFragment();
    }

    private void inflateButtonFragment() {
        Fragment signUpButtonFragment = Fragment.instantiate(this, SignUpButtonFragment.class.getName());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.sign_up_button_fragment, signUpButtonFragment)
                .commit();
    }
}

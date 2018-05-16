package com.mark.zumo.server.store.view.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.view.signup.fragment.SignInFragment;
import com.mark.zumo.server.store.viewmodel.SignUpViewModel;

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

        inflateView();
    }

    private void inflateView() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.console_fragment, Fragment.instantiate(this, SignInFragment.class.getName()))
                .commit();
    }
}

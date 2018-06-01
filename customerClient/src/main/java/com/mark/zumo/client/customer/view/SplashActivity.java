/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.main.MainActivity;
import com.mark.zumo.client.customer.view.signup.SignUpActivity;
import com.mark.zumo.client.customer.viewmodel.SignUpViewModel;

import butterknife.ButterKnife;

/**
 * Created by Goboogi on 18. 5. 1.
 */

public class SplashActivity extends BaseActivity {

    public static final String TAG = "SplashActivity";
    private static final int SPLASH_DISPLAY_LENGTH = 1000;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);

        setupSessionObserver();
    }

    private void setupSessionObserver() {
        signUpViewModel.isSessionValid().observe(this, this::onUpdateSessionAcquired);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onUpdateSessionAcquired(boolean isSessionValid) {
        Log.d(TAG, "onUpdateSessionAcquired: isSessionValid=" + isSessionValid);
        Class<? extends AppCompatActivity> targetActivity = getTargetActivity(isSessionValid);
        startTargetActivity(targetActivity);
    }

    private void startTargetActivity(Class<? extends AppCompatActivity> targetActivity) {
        findViewById(R.id.logo).setOnClickListener(v -> {
        });

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, targetActivity);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, findViewById(R.id.logo), "logo");
                this.startActivity(intent, options.toBundle());
                this.finish();
            } else {
                this.startActivity(intent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private Class<? extends AppCompatActivity> getTargetActivity(boolean isSessionValid) {
        Class<? extends AppCompatActivity> ret;

        if (isSessionValid) {
            ret = MainActivity.class;
        } else {
            ret = SignUpActivity.class;
        }

        return ret;
    }
}
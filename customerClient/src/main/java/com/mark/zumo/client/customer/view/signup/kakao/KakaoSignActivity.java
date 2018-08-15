/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.signup.kakao;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.mark.zumo.client.customer.R;

/**
 * Created by mark on 18. 6. 7.
 */
public class KakaoSignActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SESSION_OPENED = 1;
    public static final int RESULT_CODE_SESSION_FAILED = -1;

    private static final String TAG = "KakaoSignActivity";

    private ISessionCallback sessionCallback;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kakao_sign);

        sessionCallback = getSessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        setResult(RESULT_CODE_SESSION_FAILED);
    }

    @NonNull
    private ISessionCallback getSessionCallback() {
        return new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                Log.d(TAG, "onSessionOpened");
                setResult(RESULT_CODE_SESSION_OPENED);
                finish();
            }

            @Override
            public void onSessionOpenFailed(final KakaoException exception) {
                Log.e(TAG, "onSessionOpenFailed", exception);
                finish();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }
}

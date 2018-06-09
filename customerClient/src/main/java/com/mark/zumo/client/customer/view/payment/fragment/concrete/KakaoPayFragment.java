/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.payment.fragment.concrete;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.view.signup.kakao.KakaoSignActivity;
import com.mark.zumo.client.customer.viewmodel.payment.KakaoPayViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 7.
 */
public class KakaoPayFragment extends Fragment {

    private static final String TAG = "KakaoPayFragment";

    private static final int REQ_CODE_KAKAO_PAY = 10;
    private static final int REQ_CODE_KAKAO_SIGN = 11;

    @BindView(R.id.web_view) WebView webView;

    private KakaoPayViewModel kakaoPayViewModel;
    private ISessionCallback sessionCallback;

    private PaymentReadyResponse paymentReadyResponse;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kakaoPayViewModel = ViewModelProviders.of(this).get(KakaoPayViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kakao_pay, container, false);
        ButterKnife.bind(this, view);

        sessionCallback = getSessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            Intent intent = new Intent(getActivity(), KakaoSignActivity.class);
            startActivityForResult(intent, REQ_CODE_KAKAO_SIGN);
            Session.getCurrentSession().removeCallback(sessionCallback);
        }

        return view;
    }

    @NonNull
    private ISessionCallback getSessionCallback() {
        return new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                String accessToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
                inflateView(accessToken);
            }

            @Override
            public void onSessionOpenFailed(final KakaoException exception) {
                Log.d(TAG, "onSessionOpenFailed: ");
            }
        };
    }

    private void inflateView(String accessToken) {
        String orderUuid = getArguments().getString(PaymentActivity.KEY_ORDER_UUID);
        kakaoPayViewModel.preparePayment(orderUuid, accessToken).observe(this, this::onLoadPaymentReadyResponse);
        webView.setVisibility(View.VISIBLE);
    }

    private void loadUrl(String url) {
        WebViewClient client = new KakaoWebViewClient();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(client);
        webView.loadUrl(url);
    }

    private void startKakaoPayApp(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivityForResult(intent, REQ_CODE_KAKAO_PAY);
    }

    private void onLoadPaymentReadyResponse(PaymentReadyResponse readyResponse) {
        this.paymentReadyResponse = readyResponse;
        loadUrl(readyResponse.nextRedirectMobileUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQ_CODE_KAKAO_PAY:
                Log.d(TAG, "onActivityResult: requestCode-" + requestCode + ", resultCode-" + resultCode);
                break;

            case REQ_CODE_KAKAO_SIGN:
                if (resultCode == KakaoSignActivity.RESULT_CODE_SESSION_OPENED) {
                    Session.getCurrentSession().addCallback(sessionCallback);
                    Session.getCurrentSession().checkAndImplicitOpen();
                } else if (resultCode == KakaoSignActivity.RESULT_CODE_SESSION_FAILED) {
                } else {

                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onSuccessCreatePaymentToken(MenuOrder menuOrder) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent();
        intent.putExtra(PaymentActivity.KEY_ORDER_UUID, menuOrder.uuid);
        activity.setResult(PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS, intent);
        activity.finish();
    }

    private class KakaoWebViewClient extends WebViewClient {

        private static final String INTENT_KAKAO_PAY = "intent://kakaopay/pg";
        private static final String KEY_PG_TOKEN = "pg_token";

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl());
            Uri url = request.getUrl();

            if (url.toString().startsWith(INTENT_KAKAO_PAY)) {
                startKakaoPayApp(Uri.parse(paymentReadyResponse.androidAppScheme));
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);

            if (url.startsWith(INTENT_KAKAO_PAY)) {
                startKakaoPayApp(Uri.parse(paymentReadyResponse.androidAppScheme));
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, final WebResourceRequest request) {
            Uri url = request.getUrl();
            Log.d(TAG, "shouldInterceptRequest: " + url);

            if (url.toString().contains(KEY_PG_TOKEN)) {
                String pgToken = url.getQueryParameter(KEY_PG_TOKEN);
                String orderUuid = getArguments().getString(PaymentActivity.KEY_ORDER_UUID);
                kakaoPayViewModel.postTokenInfo(orderUuid, pgToken)
                        .observe(KakaoPayFragment.this, KakaoPayFragment.this::onSuccessCreatePaymentToken);
            }

            return super.shouldInterceptRequest(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
            Log.d(TAG, "shouldInterceptRequest: " + url);
            return super.shouldInterceptRequest(view, url);
        }
    }
}

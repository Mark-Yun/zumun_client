/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 6.
 */
public class PaymentActivity extends AppCompatActivity {

    public static final String KEY_ORDER_UUID = "order_uuid";
    public static final String KEY_KAKAO_URL = "url";
    public static final String KEY_KAKAO_SCHEME = "scheme";
    public static final int REQ_CODE_PAYMENT = 0;
    public static final int RESEULT_CODE_PAYMENT_SUCCESS = 1;
    public static final int RESEULT_CODE_PAYMENT_FAILED = 2;
    private static final String TAG = "PaymentActivity";
    private static final int REQ_CODE_KAKAO = 10;

    @BindView(R.id.web_view) WebView webView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        setResult(RESEULT_CODE_PAYMENT_FAILED);

        inflateWebView();
        startKakaoPayApp();
    }

    private void inflateWebView() {
        String url = getIntent().getStringExtra(KEY_KAKAO_URL);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    private void startKakaoPayApp() {
        String scheme = getIntent().getStringExtra(KEY_KAKAO_SCHEME);
        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
        startActivityForResult(intent2, REQ_CODE_KAKAO);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode-" + requestCode + " resultCode-" + resultCode + " data-" + data);
        switch (requestCode) {
            case REQ_CODE_KAKAO:
        }
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.customer.view.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.payment.fragment.concrete.KakaoPayFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 6.
 */
public class PaymentActivity extends AppCompatActivity {

    public static final String KEY_ORDER_UUID = "order_uuid";

    public static final int REQ_CODE_PAYMENT = 0;
    public static final int RESULT_CODE_PAYMENT_SUCCESS = 1;
    public static final int RESULT_CODE_PAYMENT_FAILED = 2;

    public static final String PAYMENT_TYPE = "payment_type";
    public static final String KAKAO_PAY = "kakao_pay";

    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        setResult(RESULT_CODE_PAYMENT_FAILED);

        inflateKakaoPay();
    }

    private void inflateKakaoPay() {
        Fragment fragment = Fragment.instantiate(this, KakaoPayFragment.class.getName(), getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.payment_main, fragment)
                .commit();
    }
}

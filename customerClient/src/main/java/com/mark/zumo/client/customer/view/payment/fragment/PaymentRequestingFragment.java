/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.customer.view.payment.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.view.payment.fragment.concrete.KakaoPayFragment;
import com.mark.zumo.client.customer.viewmodel.PaymentViewModel;

import java.util.Objects;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 24.
 */
public class PaymentRequestingFragment extends Fragment {
    private PaymentViewModel paymentViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        String paymentType = Objects.requireNonNull(getArguments()).getString(PaymentActivity.PAYMENT_TYPE);
        switch (Objects.requireNonNull(paymentType)) {
            case PaymentActivity.KAKAO_PAY:
                postKakaoToken();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requesting_payment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void postKakaoToken() {
        String orderUuid = Objects.requireNonNull(getArguments()).getString(PaymentActivity.KEY_ORDER_UUID);
        String tId = getArguments().getString(KakaoPayFragment.KEY_TID);
        String pgToken = getArguments().getString(KakaoPayFragment.KEY_PG_TOKEN);

        paymentViewModel.postKakaoTokenInfo(orderUuid, tId, pgToken).observe(this, this::onSuccessCreatePaymentToken);
    }

    private void onSuccessCreatePaymentToken(PaymentToken paymentToken) {
        Intent intent = new Intent();
        intent.putExtra(PaymentActivity.KEY_ORDER_UUID, paymentToken.menuOrderUuid);
        FragmentActivity activity = getActivity();
        Objects.requireNonNull(activity).setResult(PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS, intent);
        activity.finish();
    }

}

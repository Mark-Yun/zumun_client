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
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.view.payment.fragment.concrete.KakaoPayFragment;
import com.mark.zumo.client.customer.viewmodel.PaymentViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 24.
 */
public class PaymentRequestingFragment extends Fragment {

    @BindView(R.id.progress_image) AppCompatImageView progressImage;

    private PaymentViewModel paymentViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        String paymentType = Objects.requireNonNull(getArguments()).getString(PaymentActivity.PAYMENT_TYPE);
        switch (Objects.requireNonNull(paymentType)) {
            case PaymentActivity.KAKAO_PAY:
                approvePayment();
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
        animateBell();

        return view;
    }

    public void animateBell() {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
        progressImage.setAnimation(shake);
    }

    private void approvePayment() {
        String orderUuid = Objects.requireNonNull(getArguments()).getString(PaymentActivity.KEY_ORDER_UUID);
        String tId = getArguments().getString(KakaoPayFragment.KEY_TID);
        String pgToken = getArguments().getString(KakaoPayFragment.KEY_PG_TOKEN);

        paymentViewModel.approvePayment(orderUuid, tId, pgToken).observe(this, this::onSuccessCreatePaymentToken);
    }

    private void onSuccessCreatePaymentToken(String menuOrderUuid) {
        Intent intent = new Intent();
        intent.putExtra(PaymentActivity.KEY_ORDER_UUID, menuOrderUuid);
        FragmentActivity activity = getActivity();
        Objects.requireNonNull(activity).setResult(PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS, intent);
        activity.finish();
    }
}

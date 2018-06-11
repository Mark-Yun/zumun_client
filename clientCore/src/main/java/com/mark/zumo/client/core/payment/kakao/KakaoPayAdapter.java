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
package com.mark.zumo.client.core.payment.kakao;

import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.PaymentService;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayService;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayServiceProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 6.
 */
public enum KakaoPayAdapter {
    INSTANCE;

    public static final String TAG = "KakaoPayAdapter";

    private KakaoPayService kakaoPayService;
    private PaymentService paymentService;

    KakaoPayAdapter() {
        paymentService = AppServerServiceProvider.INSTANCE.buildPaymentService();
    }

    public void buildService(String accessToken) {
        kakaoPayService = KakaoPayServiceProvider.INSTANCE.buildService(accessToken);
    }

    public Maybe<MenuOrder> createPaymentToken(PaymentToken paymentToken) {
        return paymentService.createPaymentToken(paymentToken)
                .doOnError(throwable -> Log.e(TAG, "createPaymentToken: ", throwable))
                .retryWhen(flowable -> flowable.flatMap(error -> Flowable.timer(3, TimeUnit.SECONDS)))
                .retry(3);
    }

    public Maybe<PaymentReadyResponse> preparePayment(final PaymentReadyRequest paymentReadyRequest) {
        return kakaoPayService.readyPayment(
                paymentReadyRequest.cId,
                paymentReadyRequest.partnerOrderId,
                paymentReadyRequest.partnerUserId,
                paymentReadyRequest.itemName,
                paymentReadyRequest.itemCode,
                paymentReadyRequest.quantity,
                paymentReadyRequest.totalAmount,
                paymentReadyRequest.taxFreeAmount,
                paymentReadyRequest.vatAmount,
                paymentReadyRequest.approvalUrl,
                paymentReadyRequest.cancelUrl,
                paymentReadyRequest.failUrl,
                paymentReadyRequest.availableCards,
                paymentReadyRequest.paymentMethodType,
                paymentReadyRequest.installMonth,
                paymentReadyRequest.customJson)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<PaymentApprovalResponse> approvalPayment(final String accessToken,
                                                          final PaymentApprovalRequest paymentApprovalRequest) {

        return kakaoPayService.approvalPayment(accessToken,
                paymentApprovalRequest.cId,
                paymentApprovalRequest.tId,
                paymentApprovalRequest.partnerOrderId,
                paymentApprovalRequest.partnerUserId,
                paymentApprovalRequest.pgToken,
                paymentApprovalRequest.payload,
                paymentApprovalRequest.totalAmount);

    }
}

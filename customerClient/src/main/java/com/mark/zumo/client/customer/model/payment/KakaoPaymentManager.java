/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.payment;

import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.KakaoPayAdapter;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayService;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 21.
 */
public enum KakaoPaymentManager {
    INSTANCE;

    private final static String TAG = "KakaoPaymentManager";

    private final KakaoPayAdapter kakaoPayAdapter;
    private String accessToken;

    KakaoPaymentManager() {
        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
    }

    public Maybe<PaymentToken> createPaymentToken(String menuOrderUuid, String tid, String pgToken) {
        PaymentToken paymentToken = new PaymentToken(menuOrderUuid, tid, pgToken, accessToken);
        Log.d(TAG, "createPaymentToken: " + paymentToken);
        return kakaoPayAdapter.createPaymentToken(paymentToken)
                .subscribeOn(Schedulers.io());
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        kakaoPayAdapter.buildService(accessToken);
    }

    public Maybe<PaymentReadyResponse> preparePayment(final MenuOrder menuOrder) {
        PaymentReadyRequest paymentReadyRequest = new PaymentReadyRequest.Builder()
                .setCId(KakaoPayService.CID)
                .setPartnerOrderId(menuOrder.uuid)
                .setPartnerUserId(menuOrder.customerUuid)
                .setItemName(menuOrder.orderName)
                .setTotalAmount(menuOrder.totalPrice)
                .setQuantity(menuOrder.totalQuantity)
                .setTaxFreeAmount(0)
                .setApprovalUrl(PaymentReadyRequest.REDIRECT_URL_SUCCESS)
                .setCancelUrl(PaymentReadyRequest.REDIRECT_URL_CANCEL)
                .setFailUrl(PaymentReadyRequest.REDIRECT_URL_FAIL)
                .build();

        return kakaoPayAdapter.preparePayment(paymentReadyRequest)
                .subscribeOn(Schedulers.io());
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.payment;

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

    private KakaoPayAdapter kakaoPayAdapter;
    private String accessToken;

    KakaoPaymentManager() {
        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
    }

    private static String approvalUrl(MenuOrder menuOrder) {
        return "https://developers.kakao.com/success";
    }

    private static String cancelUrl(MenuOrder menuOrder) {
        return "https://developers.kakao.com/fail";
    }

    private static String failUrl(MenuOrder menuOrder) {
        return "https://developers.kakao.com/cancel";
    }

    public Maybe<MenuOrder> createPaymentToken(String menuOrderUuid, String tid, String pgToken) {
        PaymentToken paymentToken = new PaymentToken(menuOrderUuid, tid, pgToken, accessToken);
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
                .setItemName(menuOrder.name)
                .setTotalAmount(menuOrder.totalPrice)
                .setQuantity(menuOrder.totalQuantity)
                .setTaxFreeAmount(0)
                .setApprovalUrl(approvalUrl(menuOrder))
                .setCancelUrl(cancelUrl(menuOrder))
                .setFailUrl(failUrl(menuOrder))
                .build();

        return kakaoPayAdapter.preparePayment(paymentReadyRequest)
                .subscribeOn(Schedulers.io());
    }
}

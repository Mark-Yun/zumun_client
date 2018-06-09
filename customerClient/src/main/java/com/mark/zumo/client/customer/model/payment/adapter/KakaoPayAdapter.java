/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.customer.model.payment.adapter;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.PaymentService;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.KakaoPayService;
import com.mark.zumo.client.core.payment.kakao.KakaoPayServiceProvider;
import com.mark.zumo.client.core.payment.kakao.entity.PaidDetailResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 6.
 */
public enum KakaoPayAdapter implements PaymentAdapter {

    INSTANCE;

    private static final String CID = "TC0ONETIME";

    private KakaoPayService kakaoPayService;
    private PaymentService paymentService;

    private String accessToken;

    KakaoPayAdapter() {
        paymentService = AppServerServiceProvider.INSTANCE.buildPaymentService();
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

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        kakaoPayService = KakaoPayServiceProvider.INSTANCE.buildService(accessToken);
    }

    public Maybe<PaidDetailResponse> paidDetail(String tId) {
        return kakaoPayService.paiedDetail(CID, tId)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> createPaymentToken(String menuOrderUuid, String pgToken) {
        PaymentToken paymentToken = new PaymentToken(menuOrderUuid, accessToken, pgToken);
        return paymentService.createPaymentToken(paymentToken)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<PaymentReadyResponse> preparePayment(final MenuOrder menuOrder) {
        PaymentReadyRequest paymentReadyRequest = new PaymentReadyRequest.Builder()
                .setCId(CID)
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
}

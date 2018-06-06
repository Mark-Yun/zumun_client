/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.payment.adapter;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.KakaoPayService;
import com.mark.zumo.client.core.payment.kakao.KakaoPayServiceProvider;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 6.
 */
class KakaoPayAdapter implements PaymentAdapter {

    private static final String TEST_CID = "TC0ONETIME";

    private KakaoPayService payService;

    public KakaoPayAdapter() {
        payService = KakaoPayServiceProvider.INSTANCE.service;
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

    @Override
    public Maybe<PaymentReadyResponse> preparePayment(final MenuOrder menuOrder) {
        PaymentReadyRequest paymentReadyRequest = new PaymentReadyRequest.Builder()
                .setCId(TEST_CID)
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

        return payService.readyPayment(
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

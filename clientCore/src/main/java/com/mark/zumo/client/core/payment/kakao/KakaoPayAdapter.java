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

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayService;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayServiceProvider;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 6.
 */
public enum KakaoPayAdapter {
    INSTANCE;

    private static final String TAG = "KakaoPayAdapter";
    private KakaoPayService kakaoPayService;

    KakaoPayAdapter() {
    }

    public KakaoPayService buildService(String accessToken) {
        return kakaoPayService = KakaoPayServiceProvider.INSTANCE.buildService(accessToken);
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

    public Maybe<PaymentApprovalResponse> approvalPayment(final MenuOrder menuOrder, final String pgToken, final String tId) {
        PaymentApprovalRequest paymentApprovalRequest = new PaymentApprovalRequest.Builder()
                .setcId(KakaoPayService.CID)
                .setPartnerOrderId(menuOrder.uuid)
                .setPartnerUserId(menuOrder.customerUuid)
                .setPgToken(pgToken)
                .settId(tId)
                .setTotalAmount(menuOrder.totalPrice)
                .build();

        return kakaoPayService.approvalPayment(paymentApprovalRequest.cId,
                paymentApprovalRequest.tId,
                paymentApprovalRequest.partnerOrderId,
                paymentApprovalRequest.partnerUserId,
                paymentApprovalRequest.pgToken,
                paymentApprovalRequest.payload,
                paymentApprovalRequest.totalAmount);

    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao;

import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mark on 18. 5. 30.
 */
public interface KakaoPayService {
    String URL = "https://kapi.kakao.com";

    @POST("/v1/payment/ready")
    Maybe<PaymentReadyResponse> readyPayment(@Body PaymentReadyRequest readyRequest);

    @POST("/v1/payment/approve")
    Maybe<PaymentApprovalResponse> approvalPayment(@Body PaymentApprovalRequest approvalRequest);
}

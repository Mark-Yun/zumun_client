/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mark on 18. 6. 9.
 */
public interface PaymentService {
    String URL = "https://9e9owpd0lk.execute-api.ap-northeast-2.amazonaws.com/api/";

    @POST("payment/kakaopay")
    Maybe<PaymentToken> createPaymentToken(@Body PaymentToken paymentToken);
}

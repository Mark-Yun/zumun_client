/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.payment;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.customer.model.payment.adapter.PayType;
import com.mark.zumo.client.customer.model.payment.adapter.PaymentAdapter;
import com.mark.zumo.client.customer.model.payment.adapter.PaymentAdapterFactory;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 5. 21.
 */
public enum PaymentManager {
    INSTANCE;

    public Maybe<PaymentReadyResponse> preparePayment(PayType type, MenuOrder menuOrder) {
        PaymentAdapter paymentAdapter = PaymentAdapterFactory.create(type);
        return paymentAdapter.preparePayment(menuOrder);
    }
}

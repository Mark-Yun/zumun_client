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

package com.mark.zumo.client.customer.model.payment.adapter;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 5. 21.
 */
public interface PaymentAdapter {

    Maybe<PaymentReadyResponse> preparePayment(MenuOrder menuOrder);
}

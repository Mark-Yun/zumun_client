/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.mark.zumo.client.customer.model.payment.PaymentManager;

/**
 * Created by mark on 18. 6. 6.
 */
public class PaymentViewModel extends AndroidViewModel {

    private PaymentManager paymentManager;

    private PaymentViewModel(@NonNull final Application application) {
        super(application);

        paymentManager = PaymentManager.INSTANCE;
    }
}

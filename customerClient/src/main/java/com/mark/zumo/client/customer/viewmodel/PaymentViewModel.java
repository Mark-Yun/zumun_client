/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.customer.model.CustomerNotificationManager;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.payment.KakaoPaymentManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 6. 24.
 */
public class PaymentViewModel extends AndroidViewModel {
    private final KakaoPaymentManager kakaoPaymentManager;
    private final CustomerOrderManager customerOrderManager;
    private final CustomerNotificationManager customerNotificationManager;

    public PaymentViewModel(@NonNull final Application application) {
        super(application);
        kakaoPaymentManager = KakaoPaymentManager.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
        customerNotificationManager = CustomerNotificationManager.INSTANCE;
    }

    public LiveData<String> approvePayment(String menuOrderUuid, String tid, String pgToken) {
        MutableLiveData<String> liveData = new MutableLiveData<>();

        customerOrderManager.getMenuOrderFromDisk(menuOrderUuid)
                .flatMap(order -> kakaoPaymentManager.approvalPayment(order, pgToken, tid))
                .map(paymentApprovalResponse -> paymentApprovalResponse.partnerOrderId)
                .flatMap(customerOrderManager::updateMenuOrderStateRequested)
                .flatMap(customerOrderManager::sendToStoreOrderCreateMessage)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(customerNotificationManager::startOrderNotificationTracking)
                .doOnSuccess(x -> liveData.setValue(menuOrderUuid))
                .subscribe();

        return liveData;
    }
}

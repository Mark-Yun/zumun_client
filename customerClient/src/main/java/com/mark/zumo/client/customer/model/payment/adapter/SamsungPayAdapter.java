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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.customer.model.payment.PaymentContract;
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo;
import com.samsung.android.sdk.samsungpay.v2.SamsungPay;
import com.samsung.android.sdk.samsungpay.v2.StatusListener;
import com.samsung.android.sdk.samsungpay.v2.card.CardManager;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by mark on 18. 5. 21.
 */
class SamsungPayAdapter implements PaymentAdapter {

    public static final String TAG = "SamsungPayAdapter";

    private static final String SERVICE_ID = "SAMPLE_PRD_SERVICE_ID";

    private Context context;

    @Override
    public Maybe<PaymentReadyResponse> preparePayment(final MenuOrder menuOrder) {
        return null;
    }

    private Single<PartnerInfo> partnerInfo(SamsungPay.ServiceType serviceType, String serviceId) {
        return Single.create(e -> {
            Bundle bundle = new Bundle();
            bundle.putString(SamsungPay.PARTNER_SERVICE_TYPE, serviceType.toString());
            PartnerInfo partnerInfo = new PartnerInfo(serviceId, bundle);
            e.onSuccess(partnerInfo);
        });
    }

    private Single<SamsungPay> samsungPay(final PartnerInfo partnerInfo) {
        return Single.create(e -> {
            SamsungPay samsungPay = new SamsungPay(context, partnerInfo);
            e.onSuccess(samsungPay);
        });
    }

    private Single<CardManager> cardManager(final PartnerInfo partnerInfo) {
        return Single.create(e -> {
            CardManager cardManager = new CardManager(context, partnerInfo);
            e.onSuccess(cardManager);
        });
    }

    private Single<PaymentManager> paymentManager(final PartnerInfo partnerInfo) {
        return Single.create(e -> {
            PaymentManager paymentManager = new PaymentManager(context, partnerInfo);
            e.onSuccess(paymentManager);
        });
    }

    private Single<Integer> checkSamsungPayStatus(final SamsungPay samsungPay) {
        return Single.create(e -> {
            try {
                /*
                 * Method to get the Samsung Pay status on the device.
                 * Partner (Issuers, Merchants, Wallet providers, and so on) applications must call this method to
                 * check the current status of Samsung Pay before doing any operation.
                 */
                samsungPay.getSamsungPayStatus(new StatusListener() {
                    @Override
                    public void onSuccess(int status, Bundle bundle) {
                        switch (status) {
                            case SamsungPay.SPAY_NOT_SUPPORTED:
                                // Samsung Pay is not supported
                                e.onSuccess(PaymentContract.ReadyStatus.NOT_SUPPORT);
                                break;
                            case SamsungPay.SPAY_NOT_READY:
                                // Activate Samsung Pay or update Samsung Pay, if needed
                                e.onSuccess(PaymentContract.ReadyStatus.NOT_READY);
                                break;
                            case SamsungPay.SPAY_READY:
                                // Samsung Pay is ready
                                e.onSuccess(PaymentContract.ReadyStatus.READY);
                                break;
                            default:
                                // Not expected result
                                e.onSuccess(PaymentContract.ReadyStatus.ERROR);
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errorCode, Bundle bundle) {
                        e.onSuccess(PaymentContract.ReadyStatus.ERROR);
                        Log.d(TAG, "checkSamsungPayStatus onFail() : " + errorCode);
                    }
                });

                /*
                 *  Thrown if the callback passed is null.
                 */
            } catch (NullPointerException exception) {
                Log.e(TAG, "checkSamsungPayStatus: ", exception);
            }
        });

    }
}

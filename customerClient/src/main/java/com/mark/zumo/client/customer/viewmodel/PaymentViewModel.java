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

import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.customer.model.payment.KakaoPaymentManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 6. 24.
 */
public class PaymentViewModel extends AndroidViewModel {
    private final KakaoPaymentManager kakaoPaymentManager;

    public PaymentViewModel(@NonNull final Application application) {
        super(application);
        kakaoPaymentManager = KakaoPaymentManager.INSTANCE;
    }

    public LiveData<PaymentToken> postKakaoTokenInfo(String menuOrderUuid, String tid, String pgToken) {
        MutableLiveData<PaymentToken> liveData = new MutableLiveData<>();

        kakaoPaymentManager.createPaymentToken(menuOrderUuid, tid, pgToken)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }
}

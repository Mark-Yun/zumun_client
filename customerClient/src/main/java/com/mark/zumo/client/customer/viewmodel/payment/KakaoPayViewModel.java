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

package com.mark.zumo.client.customer.viewmodel.payment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.payment.KakaoPaymentManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 6.
 */
public class KakaoPayViewModel extends AndroidViewModel {

    private final KakaoPaymentManager kakaoPaymentManager;
    private final CustomerOrderManager customerOrderManager;

    private final CompositeDisposable compositeDisposable;

    public KakaoPayViewModel(@NonNull final Application application) {
        super(application);

        kakaoPaymentManager = KakaoPaymentManager.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<PaymentReadyResponse> preparePayment(@NonNull String orderUuid, @NonNull String accessToken) {

        kakaoPaymentManager.setAccessToken(accessToken);

        MutableLiveData<PaymentReadyResponse> liveData = new MutableLiveData<>();

        customerOrderManager.getMenuOrderFromDisk(orderUuid)
                .flatMap(kakaoPaymentManager::preparePayment)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}

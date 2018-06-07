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

import com.mark.zumo.client.core.payment.kakao.entity.PaidDetailResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;
import com.mark.zumo.client.customer.model.OrderManager;
import com.mark.zumo.client.customer.model.payment.adapter.KakaoPayAdapter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 6.
 */
public class KakaoPayViewModel extends AndroidViewModel {

    private KakaoPayAdapter kakaoPayAdapter;
    private OrderManager orderManager;

    private CompositeDisposable compositeDisposable;

    public KakaoPayViewModel(@NonNull final Application application) {
        super(application);

        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
        orderManager = OrderManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<PaymentReadyResponse> preparePayment(@NonNull String orderUuid, @NonNull String accessToken) {

        kakaoPayAdapter.setAccessToken(accessToken);

        MutableLiveData<PaymentReadyResponse> liveData = new MutableLiveData<>();

        orderManager.getMenuOrderFromDisk(orderUuid)
                .flatMapMaybe(kakaoPayAdapter::preparePayment)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<PaidDetailResponse> paiedDetail(String tId) {
        MutableLiveData<PaidDetailResponse> liveData = new MutableLiveData<>();

        kakaoPayAdapter.paidDetail(tId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }
}

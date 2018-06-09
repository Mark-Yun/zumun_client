/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.util.DebugUtil;
import com.mark.zumo.client.store.model.entity.OrderBucket;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderManager {
    INSTANCE;

    private OrderRepository orderRepository;
    private OrderBucket acceptedOrderBucket;
    private OrderBucket requestedOrderBucket;
    private Map<String, PaymentToken> paymentTokenMap;

    OrderManager() {
        acceptedOrderBucket = new OrderBucket();
        requestedOrderBucket = new OrderBucket();

        orderRepository = OrderRepository.INSTANCE;
    }

    public void putRequestedOrderBucket(PaymentToken paymentToken) {
        paymentTokenMap.put(paymentToken.menuOrderUuid, paymentToken);

        orderRepository.getMenuOrderFromApi(paymentToken.menuOrderUuid)
                .subscribeOn(Schedulers.io())
                .doOnNext(requestedOrderBucket::addOrder)
                .subscribe();
    }

    public Observable<OrderBucket> requestedOrderBucket() {
        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(requestedOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Observable<OrderBucket> acceptedOrderBucket() {
        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(acceptedOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Single<List<MenuOrder>> getAcceptedOrderList() {
        return Observable.create((ObservableOnSubscribe<MenuOrder>) e -> {
            for (int i = 0; i < 10; i++) {
                e.onNext(DebugUtil.menuOrder());
            }
            e.onComplete();
        }).toList().subscribeOn(Schedulers.computation());
    }

    public Observable<MenuOrder> getRequestedOrderList() {
        return Observable.create((ObservableOnSubscribe<MenuOrder>) e -> {
            for (int i = 0; i < 10; i++) {
                e.onNext(DebugUtil.menuOrder());
            }
        }).subscribeOn(Schedulers.computation());
    }

    public Observable<List<OrderDetail>> getOrderDetailList(String orderUuid) {
        return Observable.create((ObservableOnSubscribe<List<OrderDetail>>) e -> {
            e.onNext(DebugUtil.orderDetailList(orderUuid));
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return Observable.create((ObservableOnSubscribe<List<MenuOption>>) e -> {
            e.onNext(DebugUtil.menuOptionListFromOrder());
        }).subscribeOn(Schedulers.io());
    }
}

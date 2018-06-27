/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.payment.kakao.KakaoPayAdapter;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.store.model.entity.OrderBucket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderManager {
    INSTANCE;

    public static final String TAG = "OrderManager";
    private OrderRepository orderRepository;
    private KakaoPayAdapter kakaoPayAdapter;

    private OrderBucket acceptedOrderBucket;
    private OrderBucket requestedOrderBucket;
    private Map<String, PaymentToken> paymentTokenMap;

    OrderManager() {

        paymentTokenMap = new HashMap<>();

        orderRepository = OrderRepository.INSTANCE;
        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
    }

    public void putRequestedOrderBucket(PaymentToken paymentToken) {
        paymentTokenMap.put(paymentToken.menuOrderUuid, paymentToken);
        orderRepository.savePaymentToken(paymentToken);
        Log.d(TAG, "putRequestedOrderBucket: " + paymentToken);

        orderRepository.getMenuOrderFromApi(paymentToken.menuOrderUuid)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(requestedOrderBucket::addOrder)
                .subscribe();
    }

    private OrderBucket createRequestedOrderBucket(String storeUuid) {
        requestedOrderBucket = new OrderBucket();

        orderRepository.getMenuOrderListByStoreUuid(storeUuid, 0, 30)
                .subscribeOn(Schedulers.io())
                .doOnNext(requestedOrderBucket::addOrderList)
                .subscribe();

        return requestedOrderBucket;
    }

    private OrderBucket createAcceptedOrderBucket(String storeUuid) {
        acceptedOrderBucket = new OrderBucket();

        orderRepository.getMenuOrderListByStoreUuid(storeUuid, 0, 30)
                .subscribeOn(Schedulers.io())
                .doOnNext(acceptedOrderBucket::addOrderList)
                .subscribe();

        return acceptedOrderBucket;
    }

    public Observable<OrderBucket> requestedOrderBucket(String storeUuid) {
        if (requestedOrderBucket == null) {
            requestedOrderBucket = createRequestedOrderBucket(storeUuid);
        }

        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(requestedOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Observable<OrderBucket> acceptedOrderBucket(String storeUuid) {
        if (acceptedOrderBucket == null) {
            acceptedOrderBucket = createAcceptedOrderBucket(storeUuid);
        }

        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(acceptedOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Observable<List<OrderDetail>> getOrderDetailList(String orderUuid) {
        return orderRepository.getOrderDetailListByOrderUuid(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> acceptOrder(MenuOrder menuOrder) {
        return orderRepository.getPaymentToken(menuOrder.uuid)
                .flatMap(paymentToken -> kakaoPayAdapter.approvalPayment(paymentToken, menuOrder))
                .map(paymentApprovalResponse -> paymentApprovalResponse.partnerOrderId)
                .map(menuOrderUuid -> requestedOrderBucket.removeOrder(menuOrderUuid))
                .flatMap(menuOrder1 -> updateOrderState(menuOrder1, MenuOrder.State.ACCEPTED))
                .doOnSuccess(order -> acceptedOrderBucket.addOrder(order))
                .subscribeOn(Schedulers.io());
    }

    private Maybe<MenuOrder> updateOrderState(MenuOrder menuOrder, MenuOrder.State menuOrderState) {
        String menuOrderUuid = menuOrder.uuid;
        int state = menuOrderState.ordinal();
        return orderRepository.updateMenuOrderState(menuOrderUuid, state)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Long> acceptAllOrder() {
        return Observable.fromIterable(acceptedOrderBucket.getOrderList())
                .flatMapMaybe(this::acceptOrder)
                .count().toMaybe();
    }

    public void clear() {
        requestedOrderBucket.clear();
        requestedOrderBucket.clear();
        paymentTokenMap.clear();
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.payment.kakao.KakaoPayAdapter;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalRequest;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;
import com.mark.zumo.client.core.payment.kakao.server.KakaoPayService;
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

    private OrderRepository orderRepository;
    private KakaoPayAdapter kakaoPayAdapter;

    private OrderBucket acceptedOrderBucket;
    private OrderBucket requestedOrderBucket;
    private Map<String, PaymentToken> paymentTokenMap;

    OrderManager() {
        acceptedOrderBucket = new OrderBucket();
        requestedOrderBucket = new OrderBucket();

        paymentTokenMap = new HashMap<>();

        orderRepository = OrderRepository.INSTANCE;
        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
    }

    public void putRequestedOrderBucket(PaymentToken paymentToken) {
        paymentTokenMap.put(paymentToken.menuOrderUuid, paymentToken);

        orderRepository.getMenuOrderFromApi(paymentToken.menuOrderUuid)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(requestedOrderBucket::addOrder)
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

    public Observable<List<OrderDetail>> getOrderDetailList(String orderUuid) {
        return orderRepository.getOrderDetailListByOrderUuid(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> acceptOrder(MenuOrder menuOrder) {
        PaymentToken paymentToken = paymentTokenMap.remove(menuOrder.uuid);

        String kakaoAccessToken = paymentToken.kakaoAccessToken;
        String pgToken = paymentToken.pgToken;
        String tid = paymentToken.tid;

        PaymentApprovalRequest approvalRequest = new PaymentApprovalRequest.Builder()
                .setcId(KakaoPayService.CID)
                .setPartnerOrderId(menuOrder.uuid)
                .setPartnerUserId(menuOrder.storeUuid)
                .setPgToken(pgToken)
                .settId(tid)
                .setTotalAmount(menuOrder.totalQuantity)
                .build();

        return kakaoPayAdapter.approvalPayment(kakaoAccessToken, approvalRequest)
                .map(paymentApprovalResponse -> requestedOrderBucket.removeOrder(paymentApprovalResponse.partnerOrderId))
                .doOnSuccess(order -> acceptedOrderBucket.addOrder(order))
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

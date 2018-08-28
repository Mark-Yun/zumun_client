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

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderManager {
    INSTANCE;

    private static final String TAG = "OrderManager";
    private final OrderRepository orderRepository;
    private final KakaoPayAdapter kakaoPayAdapter;

    private OrderBucket canceledOrderBucket;
    private OrderBucket requestedOrderBucket;
    private OrderBucket completeOrderBucket;

    OrderManager() {
        orderRepository = OrderRepository.INSTANCE;
        kakaoPayAdapter = KakaoPayAdapter.INSTANCE;
    }

    public void putRequestedOrderBucket(PaymentToken paymentToken) {
        orderRepository.savePaymentToken(paymentToken);
        Log.d(TAG, "putRequestedOrderBucket: " + paymentToken);

        if (requestedOrderBucket == null) {
            return;
        }

        orderRepository.getMenuOrderFromApi(paymentToken.menuOrderUuid)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(requestedOrderBucket::addOrder)
                .subscribe();
    }

    private void loadOrderBucket(String storeUuid, OrderBucket orderBucket, MenuOrder.State... states) {
        orderRepository.getMenuOrderListByStoreUuid(storeUuid, 0, 30)
                .map(Observable::fromIterable)
                .doOnNext(menuOrderObservable ->
                        menuOrderObservable.filter(menuOrder -> stateAnyMatch(MenuOrder.State.of(menuOrder.state), states))
                                .toList()
                                .doOnSuccess(orderBucket::setOrder)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                ).subscribeOn(Schedulers.io())
                .subscribe();
    }

    private boolean stateAnyMatch(MenuOrder.State targetState, MenuOrder.State[] states) {
        for (MenuOrder.State state : states) {
            if (targetState == state) {
                return true;
            }
        }
        return false;
    }

    public Observable<OrderBucket> requestedOrderBucket(String storeUuid) {
        if (requestedOrderBucket == null) {
            requestedOrderBucket = new OrderBucket();
            loadOrderBucket(storeUuid, requestedOrderBucket,
                    MenuOrder.State.REQUESTED,
                    MenuOrder.State.ACCEPTED);
        }

        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(requestedOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Observable<OrderBucket> canceledOrderBucket(String storeUuid) {
        if (canceledOrderBucket == null) {
            canceledOrderBucket = new OrderBucket();
            loadOrderBucket(storeUuid, canceledOrderBucket,
                    MenuOrder.State.CANCELED,
                    MenuOrder.State.REJECTED);
        }

        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(canceledOrderBucket.addEmitter(e))
        ).subscribeOn(Schedulers.computation());
    }

    public Observable<OrderBucket> completeOrderBucket(String storeUuid) {
        if (completeOrderBucket == null) {
            completeOrderBucket = new OrderBucket();
            loadOrderBucket(storeUuid, completeOrderBucket, MenuOrder.State.COMPLETE);
        }

        return Observable.create(
                (ObservableOnSubscribe<OrderBucket>) e -> e.onNext(completeOrderBucket.addEmitter(e))
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
                .map(requestedOrderBucket::getOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> rejectOrder(String orderUuid) {
        return orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.REJECTED.ordinal())
                .map(menuOrder -> menuOrder.uuid)
                .map(requestedOrderBucket::removeOrder)
                .doOnSuccess(canceledOrderBucket::addOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return orderRepository.getMenuOrderFromDisk(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Long> acceptAllOrder() {
        return Observable.fromIterable(canceledOrderBucket.getOrderList())
                .flatMapMaybe(this::acceptOrder)
                .count().toMaybe();
    }

    public void clear() {
        requestedOrderBucket.clear();
        requestedOrderBucket.clear();
    }
}

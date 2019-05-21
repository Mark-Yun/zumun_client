/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.repository.MessageHandler;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.store.model.entity.order.OrderBucket;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreOrderManager {
    INSTANCE;

    private static final String TAG = "StoreOrderManager";

    private final OrderRepository orderRepository;
    private final MessageHandler messageHandler;

    private OrderBucket canceledOrderBucket;
    private OrderBucket requestedOrderBucket;
    private OrderBucket completeOrderBucket;

    StoreOrderManager() {
        orderRepository = OrderRepository.INSTANCE;
        messageHandler = MessageHandler.INSTANCE;
    }

    public void putRequestedOrderBucket(String menuOrderUuid) {
        Log.d(TAG, "putRequestedOrderBucket: " + menuOrderUuid);

        if (requestedOrderBucket == null) {
            return;
        }
        orderRepository.getMenuOrderFromApi(menuOrderUuid)
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

                ).subscribeOn(Schedulers.io()).subscribe();
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
        }

        loadOrderBucket(storeUuid, requestedOrderBucket,
                MenuOrder.State.REQUESTED,
                MenuOrder.State.ACCEPTED,
                MenuOrder.State.COMPLETE);

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
        return orderRepository.updateMenuOrderState(menuOrder.uuid, MenuOrder.State.ACCEPTED.ordinal())
                .flatMap(menuOrder1 -> messageHandler.sendMessageOrderUpdated(menuOrder1.customerUuid, menuOrder1))
                .doOnSuccess(x -> requestedOrderBucket.notifyOnNext())
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> rejectOrder(String orderUuid) {
        return orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.REJECTED.ordinal())
                .map(menuOrder -> menuOrder.uuid)
                .map(requestedOrderBucket::removeOrder)
                .doOnSuccess(canceledOrderBucket::addOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> completeOrder(String orderUuid) {
        return orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.COMPLETE.ordinal())
                .flatMap(menuOrder1 -> messageHandler.sendMessageOrderUpdated(menuOrder1.customerUuid, menuOrder1))
                .doOnSuccess(x -> requestedOrderBucket.notifyOnNext())
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> finishOrder(String orderUuid) {
        return orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.FINISHED.ordinal())
                .flatMap(menuOrder1 -> messageHandler.sendMessageOrderUpdated(menuOrder1.customerUuid, menuOrder1))
                .map(menuOrder -> menuOrder.uuid)
                .map(requestedOrderBucket::removeOrder)
                .doOnSuccess(completeOrderBucket::addOrder)
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

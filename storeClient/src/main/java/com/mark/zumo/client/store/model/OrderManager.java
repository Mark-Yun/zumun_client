/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.repository.MessageHandler;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.repository.SessionRepository;
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

    private final SessionRepository sessionRepository;

    private final Maybe<OrderRepository> orderRepositoryMaybe;
    private final Maybe<MessageHandler> messageHandlerMaybe;

    private OrderBucket canceledOrderBucket;
    private OrderBucket requestedOrderBucket;
    private OrderBucket completeOrderBucket;

    OrderManager() {
        sessionRepository = SessionRepository.INSTANCE;

        orderRepositoryMaybe = sessionRepository.getStoreSession()
                .map(OrderRepository::getInstance);

        messageHandlerMaybe = sessionRepository.getStoreSession()
                .map(MessageHandler::getInstance);
    }

    public void putRequestedOrderBucket(String menuOrderUuid) {
        Log.d(TAG, "putRequestedOrderBucket: " + menuOrderUuid);

        if (requestedOrderBucket == null) {
            return;
        }
        orderRepositoryMaybe.flatMap(orderRepository ->
                orderRepository.getMenuOrderFromApi(menuOrderUuid)
                        .subscribeOn(Schedulers.io())
                        .doOnSuccess(requestedOrderBucket::addOrder))
                .subscribe();
    }

    private void loadOrderBucket(String storeUuid, OrderBucket orderBucket, MenuOrder.State... states) {
        orderRepositoryMaybe.flatMapObservable(orderRepository ->
                orderRepository.getMenuOrderListByStoreUuid(storeUuid, 0, 30)
                        .map(Observable::fromIterable)
                        .doOnNext(menuOrderObservable ->
                                menuOrderObservable.filter(menuOrder -> stateAnyMatch(MenuOrder.State.of(menuOrder.state), states))
                                        .toList()
                                        .doOnSuccess(orderBucket::setOrder)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe()
                        )
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
        return orderRepositoryMaybe.flatMapObservable(orderRepository -> orderRepository.getOrderDetailListByOrderUuid(orderUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> acceptOrder(MenuOrder menuOrder) {
        return orderRepositoryMaybe.flatMap(orderRepository ->
                orderRepository.updateMenuOrderState(menuOrder.uuid, MenuOrder.State.ACCEPTED.ordinal())
                        .flatMap(updatedMenuOrder -> messageHandlerMaybe.flatMap(messageHandler -> messageHandler.sendMessageAcceptedOrder(updatedMenuOrder)))
                        .doOnSuccess(x -> requestedOrderBucket.notifyOnNext())
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> rejectOrder(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository ->
                orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.REJECTED.ordinal())
                        .map(menuOrder -> menuOrder.uuid)
                        .map(requestedOrderBucket::removeOrder)
                        .doOnSuccess(canceledOrderBucket::addOrder))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> completeOrder(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository ->
                orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.COMPLETE.ordinal())
                        .flatMap(updatedOrder -> messageHandlerMaybe.flatMap(messageHandler -> messageHandler.sendMessageCompleteOrder(updatedOrder)))
                        .map(menuOrder -> menuOrder.uuid)
                        .map(requestedOrderBucket::removeOrder)
                        .doOnSuccess(completeOrderBucket::addOrder)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository ->
                orderRepository.getMenuOrderFromDisk(orderUuid)
        ).subscribeOn(Schedulers.io());
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

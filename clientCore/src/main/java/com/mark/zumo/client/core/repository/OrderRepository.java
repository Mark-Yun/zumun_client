/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.util.ListComparator;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderRepository {
    INSTANCE;

    private static final String TAG = "OrderRepository";

    private final NetworkRepository networkRepository;
    private final DiskRepository diskRepository;

    OrderRepository() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Maybe<MenuOrder> createMenuOrder(List<OrderDetail> orderDetailCollection) {
        return networkRepository.createOrder(orderDetailCollection)
                .doOnSuccess(diskRepository::insertMenuOrder);
    }

    public Observable<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return Observable.just(new ArrayList<OrderDetail>())
                .map(arrayList -> {
                    arrayList.add(orderDetail);
                    return arrayList;
                })
                .flatMapMaybe(networkRepository::createOrder)
                .doOnError(throwable -> Log.e(TAG, "createMenuOrder: ", throwable))
                .doOnNext(diskRepository::insertMenuOrder);
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return diskRepository.getMenuOrder(orderUuid)
                .doOnError(throwable -> Log.e(TAG, "getMenuOrderFromDisk: ", throwable))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        return networkRepository.getMenuOrder(orderUuid)
                .doOnSuccess(menuOrder -> diskRepository.insertMenuOrder(menuOrder))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<OrderDetail>> getOrderDetailListByOrderUuid(String orderUuid) {
        Maybe<List<OrderDetail>> orderDetailListDB = diskRepository.getOrderDetailListByMenuOrderUuid(orderUuid)
                .doOnError(throwable -> Log.e(TAG, "getOrderDetailListByOrderUuid: ", throwable));
        Maybe<List<OrderDetail>> orderDetailListApi = networkRepository.getOrderDetailList(orderUuid)
                .doOnError(throwable -> Log.e(TAG, "getOrderDetailListByOrderUuid: ", throwable))
                .doOnSuccess(diskRepository::insertOrderDetailList);

        return Maybe.merge(orderDetailListDB, orderDetailListApi)
                .toObservable()
                .distinctUntilChanged(new ListComparator<>());
    }

    public Observable<List<MenuOrder>> getMenuOrderListByCustomerUuid(String customerUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = diskRepository.getMenuOrderByCustomerUuid(customerUuid, offset, limit)
                .doOnError(throwable -> Log.e(TAG, "getMenuOrderListByCustomerUuid: ", throwable));
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository.getMenuOrderListByCustomerUuid(customerUuid, offset, limit)
                .doOnError(throwable -> Log.e(TAG, "getMenuOrderListByCustomerUuid: ", throwable))
                .doOnSuccess(diskRepository::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged(new ListComparator<>());
    }

    public Observable<List<MenuOrder>> getMenuOrderListByStoreUuid(String storeUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = diskRepository.getMenuOrderByStoreUuid(storeUuid, offset, limit);
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository.getMenuOrderListByStoreUuid(storeUuid, offset, limit)
                .doOnSuccess(diskRepository::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged(new ListComparator<>());
    }

    public Maybe<MenuOrder> updateMenuOrderState(String menuOrderUuid, int state) {
        return networkRepository.updateMenuOrderState(menuOrderUuid, state)
                .doOnError(throwable -> Log.e(TAG, "updateMenuOrderState: ", throwable))
                .doOnSuccess(diskRepository::insertMenuOrder);
    }

    public void savePaymentToken(PaymentToken paymentToken) {
        diskRepository.insertPaymentToken(paymentToken);
    }

    public Maybe<PaymentToken> getPaymentToken(String menuOrderUuid) {
        return diskRepository.getPaymentToken(menuOrderUuid)
                .doOnSuccess(diskRepository::removePaymentToken);
    }
}

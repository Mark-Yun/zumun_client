/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.BundleUtils;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public class OrderRepository {

    public static final String ACTION_ORDER_UPDATED = "com.mark.zumo.client.zumo.action.ORDER_UPDATED";

    private static final String TAG = "OrderRepository";

    private static OrderRepository sInstance;
    private static Bundle session;
    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private OrderRepository(final Bundle session) {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        OrderRepository.session = session;
    }

    public static OrderRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(OrderRepository.session, session)) {
            synchronized (OrderRepository.class) {
                if (sInstance == null) {
                    sInstance = new OrderRepository(session);
                }
            }
        }

        return sInstance;
    }

    public Maybe<MenuOrder> createMenuOrder(List<OrderDetail> orderDetailCollection) {
        return networkRepository.createOrder(orderDetailCollection)
                .doOnSuccess(diskRepository::insertMenuOrder);
    }

    public Maybe<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return Maybe.just(new ArrayList<OrderDetail>())
                .doOnSuccess(arrayList -> arrayList.add(orderDetail))
                .flatMap(networkRepository::createOrder)
                .doOnSuccess(diskRepository::insertMenuOrder);
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return diskRepository.getMenuOrder(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        return networkRepository.getMenuOrder(orderUuid)
                .doOnSuccess(diskRepository::insertMenuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrder(String orderUuid) {
        Maybe<MenuOrder> menuOrderDB = getMenuOrderFromDisk(orderUuid);
        Maybe<MenuOrder> menuOrderApi = getMenuOrderFromApi(orderUuid);
        return Maybe.merge(menuOrderDB, menuOrderApi)
                .distinctUntilChanged()
                .toObservable();
    }

    public Observable<List<OrderDetail>> getOrderDetailListByOrderUuid(String orderUuid) {
        Maybe<List<OrderDetail>> orderDetailListDB = diskRepository.getOrderDetailListByMenuOrderUuid(orderUuid);
        Maybe<List<OrderDetail>> orderDetailListApi = networkRepository.getOrderDetailList(orderUuid)
                .doOnSuccess(diskRepository::insertOrderDetailList);

        return Maybe.merge(orderDetailListDB, orderDetailListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOrder>> getMenuOrderListByCustomerUuid(String customerUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = diskRepository.getMenuOrderByCustomerUuid(customerUuid, offset, limit);
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository.getMenuOrderListByCustomerUuid(customerUuid, offset, limit)
                .doOnSuccess(diskRepository::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOrder>> getMenuOrderListByStoreUuid(String storeUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = diskRepository.getMenuOrderByStoreUuid(storeUuid, offset, limit);
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository.getMenuOrderListByStoreUuid(storeUuid, offset, limit)
                .doOnSuccess(diskRepository::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOrder> updateMenuOrderState(String menuOrderUuid, int state) {
        return getMenuOrderFromDisk(menuOrderUuid)
                .map(menuOrder -> menuOrder.updateState(state))
                .flatMap(menuOrder -> networkRepository.updateMenuOrderState(menuOrder.uuid, menuOrder))
                .doOnSuccess(diskRepository::insertMenuOrder)
                .doOnSuccess(x -> sendOrderUpdated());
    }

    private void sendOrderUpdated() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ContextHolder.getContext());
        localBroadcastManager.sendBroadcast(new Intent(ACTION_ORDER_UPDATED));
    }
}

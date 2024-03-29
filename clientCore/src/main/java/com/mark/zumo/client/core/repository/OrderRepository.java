/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.database.AppDatabaseProvider;
import com.mark.zumo.client.core.database.dao.MenuOrderDao;
import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderRepository {
    INSTANCE;

    public static final String ACTION_ORDER_UPDATED = "com.mark.zumo.client.zumo.action.EVENT_ORDER_UPDATED";

    private static final String TAG = "OrderRepository";

    private final MenuOrderDao menuOrderDao;

    OrderRepository() {
        menuOrderDao = AppDatabaseProvider.INSTANCE.menuOrderDao;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public Maybe<MenuOrder> createMenuOrder(List<OrderDetail> orderDetailCollection) {
        return networkRepository().createOrder(orderDetailCollection)
                .doOnSuccess(menuOrderDao::insertMenuOrder);
    }

    public Maybe<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return Maybe.just(new ArrayList<OrderDetail>())
                .doOnSuccess(arrayList -> arrayList.add(orderDetail))
                .flatMap(networkRepository()::createOrder)
                .doOnSuccess(menuOrderDao::insertMenuOrder);
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return menuOrderDao.getMenuOrderMaybe(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        return networkRepository().getMenuOrder(orderUuid)
                .doOnSuccess(menuOrderDao::insertMenuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrder(String orderUuid) {
        Maybe<MenuOrder> menuOrderDB = getMenuOrderFromDisk(orderUuid);
        Maybe<MenuOrder> menuOrderApi = getMenuOrderFromApi(orderUuid);
        return Maybe.merge(menuOrderDB, menuOrderApi)
                .distinctUntilChanged()
                .toObservable();
    }

    public Observable<MenuOrder> getMenuOrderObservable(String orderUuid) {
        return menuOrderDao.getMenuOrderFlowable(orderUuid)
                .toObservable();
    }

    public Observable<List<OrderDetail>> getOrderDetailListByOrderUuid(String orderUuid) {
        Maybe<List<OrderDetail>> orderDetailListDB = menuOrderDao.getOrderDetailListByMenuOrderUuid(orderUuid);
        Maybe<List<OrderDetail>> orderDetailListApi = networkRepository().getOrderDetailList(orderUuid)
                .doOnSuccess(menuOrderDao::insertOrderDetailList);

        return Maybe.merge(orderDetailListDB, orderDetailListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOrder>> getMenuOrderListByCustomerUuid(String customerUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = menuOrderDao.getMenuOrderByCustomerUuid(customerUuid, offset, limit);
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository().getMenuOrderListByCustomerUuid(customerUuid, offset, limit)
                .doOnSuccess(x -> menuOrderDao.deleteMenuOrderListByCustomerUuid(customerUuid))
                .doOnSuccess(menuOrderDao::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOrder>> getMenuOrderListByStoreUuid(String storeUuid, int offset, int limit) {
        Maybe<List<MenuOrder>> menuOrderListDB = menuOrderDao.getMenuOrderByStoreUuid(storeUuid, offset, limit);
        Maybe<List<MenuOrder>> menuOrderListApi = networkRepository().getMenuOrderListByStoreUuid(storeUuid, offset, limit)
                .doOnSuccess(menuOrderDao::insertMenuOrderList);

        return Maybe.merge(menuOrderListDB, menuOrderListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOrder> updateMenuOrderState(String menuOrderUuid, int state) {
        return getMenuOrderFromDisk(menuOrderUuid)
                .map(menuOrder -> menuOrder.updateState(state))
                .flatMap(menuOrder -> networkRepository().updateMenuOrderState(menuOrder.uuid, menuOrder))
                .retry(5)
                .retryWhen(errors -> errors.flatMap(error -> Flowable.timer(1, TimeUnit.SECONDS)))
                .doOnSuccess(menuOrderDao::insertMenuOrder)
                .doOnSuccess(x -> sendOrderUpdated());
    }

    private void sendOrderUpdated() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ContextHolder.getContext());
        localBroadcastManager.sendBroadcast(new Intent(ACTION_ORDER_UPDATED));
    }
}

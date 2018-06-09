/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;

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

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    OrderRepository() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Observable<MenuOrder> createMenuOrder(List<OrderDetail> orderDetailCollection) {
        return networkRepository.createOrder(orderDetailCollection)
                .doOnNext(diskRepository::insert);
    }

    public Observable<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return Observable.just(new ArrayList<OrderDetail>())
                .map(arrayList -> {
                    arrayList.add(orderDetail);
                    return arrayList;
                })
                .flatMap(orderDetailList -> networkRepository.createOrder(orderDetailList))
                .doOnNext(diskRepository::insert);
    }

    public Observable<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return diskRepository.getMenuOrder(orderUuid)
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        Maybe<MenuOrder> menuOrderApi = networkRepository.getMenuOrder(orderUuid);
        return menuOrderApi.toObservable()
                .subscribeOn(Schedulers.io());
    }
}

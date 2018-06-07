/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderManager {
    INSTANCE;

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    OrderManager() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Observable<MenuOrder> createMenuOrder(Collection<OrderDetail> orderDetailCollection) {
        //TODO: remove Test Code
        return Observable.fromCallable(DebugUtil::menuOrder)
                .subscribeOn(Schedulers.io());
//        return networkRepository.createOrder(orderDetailCollection)
//                .doOnNext(diskRepository::insert)
//                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        //TODO: remove Test Code
        return Observable.fromCallable(DebugUtil::menuOrder)
                .subscribeOn(Schedulers.io());
//        return Observable.fromCallable((Callable<ArrayList<OrderDetail>>) ArrayList::new)
//                .doOnEach(notification -> notification.getValue().add(orderDetail))
//                .flatMap(networkRepository::createOrder)
//                .doOnNext(diskRepository::insert)
//                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return Observable.fromCallable(() -> DebugUtil.menuOrder())
                .subscribeOn(Schedulers.io());

//        return diskRepository.getMenuOrder(orderUuid)
//                .toObservable()
//                .subscribeOn(Schedulers.io());
    }
}

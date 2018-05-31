package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

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
        return networkRepository.createOrder(orderDetailCollection)
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return Observable.fromCallable((Callable<ArrayList<OrderDetail>>) ArrayList::new)
                .doOnEach(notification -> notification.getValue().add(orderDetail))
                .flatMap(networkRepository::createOrder)
                .doOnNext(diskRepository::insert)
                .subscribeOn(Schedulers.io());
    }
}

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderManager {
    INSTANCE;

    private OrderRepository orderRepository;

    OrderManager() {
        orderRepository = OrderRepository.INSTANCE;
    }

    public Maybe<MenuOrder> createMenuOrder(List<OrderDetail> orderDetailList) {
        String generatedOrderName = orderDetailList.get(0).menuName;
        if (orderDetailList.size() > 1) {
            String amount = String.valueOf(orderDetailList.size() - 1);
            String etcText = ContextHolder.getContext().getString(R.string.menu_order_name_and, amount);
            generatedOrderName += " " + etcText;
        }
        final String orderName = generatedOrderName;
        return Observable.fromIterable(orderDetailList)
                .map(orderDetail -> {
                    orderDetail.menuOrderName = orderName;
                    return orderDetail;
                }).toList().flatMapMaybe(orderRepository::createMenuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return orderRepository.createMenuOrder(orderDetail)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return orderRepository.getMenuOrderFromDisk(orderUuid)
                .subscribeOn(Schedulers.io());
    }
}

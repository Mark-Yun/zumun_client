/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.repository.CustomerUserRepository;
import com.mark.zumo.client.core.repository.MessageHandler;
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

public enum CustomerOrderManager {
    INSTANCE;

    private final OrderRepository orderRepository;
    private final MessageHandler messageHandler;

    private final CustomerSessionManager customerSessionManager;
    private final CustomerUserRepository customerUserRepository;

    CustomerOrderManager() {
        customerUserRepository = CustomerUserRepository.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;

        orderRepository = OrderRepository.INSTANCE;
        messageHandler = MessageHandler.INSTANCE;
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
                }).toList().toMaybe()
                .flatMap(orderRepository::createMenuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return orderRepository.createMenuOrder(orderDetail)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return orderRepository.getMenuOrderFromDisk(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        return orderRepository.getMenuOrderFromApi(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrder(String orderUuid) {
        return orderRepository.getMenuOrder(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOrder>> getMenuOrderListByCustomerUuid(String customerUuid) {
        return orderRepository.getMenuOrderListByCustomerUuid(customerUuid, 0, 20)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<OrderDetail>> getOrderDetailListByOrderUuid(String orderUuid) {
        return orderRepository.getOrderDetailListByOrderUuid(orderUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendOrderCreateMessage(MenuOrder menuOrder) {
        return messageHandler.sendMessageCreateOrder(menuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> updateMenuOrderStateRequested(String orderUuid) {
        return orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.REQUESTED.ordinal())
                .subscribeOn(Schedulers.io());
    }
}

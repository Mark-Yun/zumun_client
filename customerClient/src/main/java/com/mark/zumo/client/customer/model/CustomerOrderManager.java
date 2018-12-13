/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.repository.MessageHandler;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.repository.SessionRepository;
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

    private final Maybe<OrderRepository> orderRepositoryMaybe;
    private final Maybe<MessageHandler> messageHandlerMaybe;

    private final CustomerSessionManager customerSessionManager;
    private final SessionRepository sessionRepository;

    CustomerOrderManager() {
        sessionRepository = SessionRepository.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;

        orderRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(OrderRepository::getInstance);
        messageHandlerMaybe = sessionRepository.getCustomerSession()
                .map(MessageHandler::getInstance);
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
                .flatMap(order -> orderRepositoryMaybe.flatMap(orderRepository -> orderRepository.createMenuOrder(order)))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> createMenuOrder(OrderDetail orderDetail) {
        return orderRepositoryMaybe.flatMap(orderRepository -> orderRepository.createMenuOrder(orderDetail))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromDisk(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository -> orderRepository.getMenuOrderFromDisk(orderUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> getMenuOrderFromApi(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository -> orderRepository.getMenuOrderFromApi(orderUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<MenuOrder> getMenuOrder(String orderUuid) {
        return orderRepositoryMaybe.flatMapObservable(orderRepository -> orderRepository.getMenuOrder(orderUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOrder>> getMenuOrderListByCustomerUuid(String customerUuid) {
        return orderRepositoryMaybe.flatMapObservable(orderRepository ->
                orderRepository.getMenuOrderListByCustomerUuid(customerUuid, 0, 20)
        ).subscribeOn(Schedulers.io());
    }

    public Observable<List<OrderDetail>> getOrderDetailListByOrderUuid(String orderUuid) {
        return orderRepositoryMaybe.flatMapObservable(orderRepository -> orderRepository.getOrderDetailListByOrderUuid(orderUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendOrderCreateMessage(MenuOrder menuOrder) {
        return messageHandlerMaybe.flatMap(messageHandler -> messageHandler.sendMessageCreateOrder(menuOrder))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> updateMenuOrderStateRequested(String orderUuid) {
        return orderRepositoryMaybe.flatMap(orderRepository -> orderRepository.updateMenuOrderState(orderUuid, MenuOrder.State.REQUESTED.ordinal()))
                .subscribeOn(Schedulers.io());
    }
}

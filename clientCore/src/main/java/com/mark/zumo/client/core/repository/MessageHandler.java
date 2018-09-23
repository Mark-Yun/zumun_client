/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.PaymentService;
import com.mark.zumo.client.core.appserver.request.SnsSendMessageRequest;
import com.mark.zumo.client.core.appserver.request.message.OrderAcceptedMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCompleteMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCreatedMessage;
import com.mark.zumo.client.core.entity.MenuOrder;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 9. 21.
 */
public enum MessageHandler {
    INSTANCE;

    MessageHandler() {
    }

    private PaymentService paymentService() {
        return AppServerServiceProvider.INSTANCE.paymentService;
    }

    public Maybe<MenuOrder> sendMessageCreateOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderCreatedMessage::new)
                .map(message -> new SnsSendMessageRequest(menuOrder.storeUuid, message))
                .flatMap(paymentService()::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendMessageAcceptedOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderAcceptedMessage::new)
                .map(message -> new SnsSendMessageRequest(menuOrder.customerUuid, message))
                .flatMap(paymentService()::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendMessageCompleteOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderCompleteMessage::new)
                .map(message -> new SnsSendMessageRequest(menuOrder.customerUuid, message))
                .flatMap(paymentService()::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }
}

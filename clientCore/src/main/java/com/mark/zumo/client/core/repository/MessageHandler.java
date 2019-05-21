/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.PaymentService;
import com.mark.zumo.client.core.appserver.request.sns.SnsSendMessageRequest;
import com.mark.zumo.client.core.appserver.request.sns.message.SnsMessage;
import com.mark.zumo.client.core.database.entity.MenuOrder;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 9. 21.
 */
public enum MessageHandler {
    INSTANCE;

    public static final String TAG = "MessageHandler";

    MessageHandler() {
    }

    private PaymentService paymentService() {
        return AppServerServiceProvider.INSTANCE.paymentService();
    }

    public Maybe<MenuOrder> sendMessageOrderUpdated(final String receiverUuid, final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(SnsMessage::createOrderUpdated)
                .map(message -> new SnsSendMessageRequest(receiverUuid, message))
                .flatMap(paymentService()::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }
}

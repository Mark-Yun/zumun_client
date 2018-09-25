/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.PaymentService;
import com.mark.zumo.client.core.appserver.request.SnsSendMessageRequest;
import com.mark.zumo.client.core.appserver.request.message.OrderAcceptedMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCompleteMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCreatedMessage;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.BundleUtils;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 9. 21.
 */
public class MessageHandler {

    public static final String TAG = "MessageHandler";
    private static MessageHandler sInstance;
    private static Bundle session;

    private final PaymentService paymentService;

    private MessageHandler(final Bundle session) {
        paymentService = AppServerServiceProvider.INSTANCE.buildPaymentService(session);
        MessageHandler.session = session;
    }

    public static MessageHandler getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(MessageHandler.session, session)) {
            synchronized (MessageHandler.class) {
                if (sInstance == null) {
                    sInstance = new MessageHandler(session);
                }
            }
        }

        return sInstance;
    }
    public Maybe<MenuOrder> sendMessageCreateOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderCreatedMessage::new)
                .doOnSuccess(snsMessage -> Log.d(TAG, "sendMessageCreateOrder: " + snsMessage))
                .map(message -> new SnsSendMessageRequest(menuOrder.storeUuid, message))
                .doOnSuccess(snsSendMessageRequest -> Log.d(TAG, "sendMessageCreateOrder: " + snsSendMessageRequest))
                .flatMap(paymentService::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendMessageAcceptedOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderAcceptedMessage::new)
                .map(message -> new SnsSendMessageRequest(menuOrder.customerUuid, message))
                .flatMap(paymentService::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOrder> sendMessageCompleteOrder(final MenuOrder menuOrder) {
        return Maybe.just(menuOrder)
                .map(order -> order.uuid)
                .map(OrderCompleteMessage::new)
                .map(message -> new SnsSendMessageRequest(menuOrder.customerUuid, message))
                .flatMap(paymentService::sendMessage)
                .map(x -> menuOrder)
                .subscribeOn(Schedulers.io());
    }
}

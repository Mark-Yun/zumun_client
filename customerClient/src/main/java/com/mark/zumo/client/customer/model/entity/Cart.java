/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.entity;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {

    public static final String TAG = "Cart";

    private List<OrderDetail> orderDetailList;
    private Collection<ObservableEmitter<Cart>> emitterCollection;

    public Cart() {
        orderDetailList = new ArrayList<>();
        emitterCollection = new ArrayList<>();
    }

    public void addCartItem(OrderDetail orderDetail) {
        Log.d(TAG, "addCartItem: " + orderDetail);
        orderDetailList.add(orderDetail);
        notifyOnNext();
        vibrationFeedback();
    }

    public Cart addEmitter(ObservableEmitter<Cart> emitter) {
        emitterCollection.add(emitter);
        return this;
    }

    private void notifyOnNext() {
        for (ObservableEmitter<Cart> emitter : emitterCollection) {
            if (emitter != null) {
                emitter.onNext(Cart.this);
            }
        }
    }

    public OrderDetail getOrderList(int position) {
        return orderDetailList.get(position);
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void clear() {
        orderDetailList.clear();
    }

    public void removeCartItem(int position) {
        Log.d(TAG, "removeCartItem: " + position);
        orderDetailList.remove(position);
        notifyOnNext();
        vibrationFeedback();
    }

    public void removeLatestCartItem() {
        removeCartItem(orderDetailList.size() - 1);
        notifyOnNext();
    }

    public int getItemCount() {
        return orderDetailList.size();
    }

    private void vibrationFeedback() {
        Vibrator vibrator = (Vibrator) ContextHolder.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }
}

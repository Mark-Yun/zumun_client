/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model.entity;

import android.util.Log;

import com.mark.zumo.client.core.database.entity.OrderDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {

    private static final String TAG = "Cart";

    private final List<OrderDetail> orderDetailList;
    private final Collection<ObservableEmitter<Cart>> emitterCollection;

    public Cart() {
        orderDetailList = new ArrayList<>();
        emitterCollection = new ArrayList<>();
    }

    public void addCartItem(OrderDetail orderDetail) {
        Log.d(TAG, "addCartItem: " + orderDetail);
        int index = hasSameMenu(orderDetail);
        if (index < 0) {
            orderDetailList.add(orderDetail);
        } else {
            mergeOrderDetail(index, orderDetail);
        }
        notifyOnNext();
    }

    public void updateCartItem(OrderDetail orderDetail, int index) {
        orderDetailList.remove(index);
        orderDetailList.add(index, orderDetail);
        notifyOnNext();
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

    public OrderDetail getOrderDetail(int position) {
        return orderDetailList.get(position);
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void clear() {
        orderDetailList.clear();
        notifyOnNext();
    }

    public void removeCartItem(int position) {
        Log.d(TAG, "removeCartItem: " + position);
        orderDetailList.remove(position);
        notifyOnNext();
    }

    public int getItemCount() {
        return orderDetailList.size();
    }

    public void removeLatestCartItem() {
        removeCartItem(orderDetailList.size() - 1);
        notifyOnNext();
    }

    public int getTotalAmount() {
        return Observable.fromIterable(orderDetailList)
                .map(orderDetail -> orderDetail.quantity)
                .reduce((integer, integer2) -> integer + integer2)
                .blockingGet(0);
    }

    private int hasSameMenu(OrderDetail orderDetail) {
        for (int i = 0; i < orderDetailList.size(); i++) {
            boolean hasSameMenu = orderDetailList.get(i).isSameMenu(orderDetail);
            if (hasSameMenu) {
                return i;
            }
        }
        return -1;
    }

    private void mergeOrderDetail(int index, OrderDetail orderDetail2) {
        if (index < 0) return;

        OrderDetail orderDetail = orderDetailList.remove(index);

        orderDetailList.add(index,
                new OrderDetail(
                        "",
                        orderDetail.storeUuid,
                        orderDetail.menuUuid,
                        orderDetail.menuName,
                        null,
                        orderDetail.menuOptionUuidList,
                        orderDetail.quantity + orderDetail2.quantity,
                        orderDetail.price + orderDetail2.price
                )
        );
    }
}

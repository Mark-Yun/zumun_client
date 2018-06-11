/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model.entity;

import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 6. 9.
 */
public class OrderBucket {

    public static final String TAG = "OrderBucket";
    private List<MenuOrder> orderList;
    private Collection<ObservableEmitter<OrderBucket>> emitterCollection;

    public OrderBucket() {
        orderList = new ArrayList<>();
        emitterCollection = new ArrayList<>();
    }

    public void addOrder(MenuOrder menuOrder) {
        orderList.add(menuOrder);
        Log.d(TAG, "addOrder: " + menuOrder);
        notifyOnNext();
    }

    public OrderBucket addEmitter(ObservableEmitter<OrderBucket> emitter) {
        emitterCollection.add(emitter);
        return this;
    }

    private void notifyOnNext() {
        for (ObservableEmitter<OrderBucket> emitter : emitterCollection) {
            if (emitter != null) {
                emitter.onNext(this);
            }
        }
    }

    public List<MenuOrder> getOrderList() {
        return orderList;
    }

    public void clear() {
        orderList.clear();
    }

    public MenuOrder removeOrder(int position) {
        MenuOrder remove = orderList.remove(position);
        notifyOnNext();
        return remove;
    }

    public MenuOrder removeOrder(String uuid) {
        MenuOrder remove = orderList.remove(getMenuOrderPosition(uuid));
        notifyOnNext();
        return remove;
    }

    private int getMenuOrderPosition(String uuid) {
        for (int i = 0; i < orderList.size(); i++) {
            MenuOrder menuOrder = orderList.get(i);
            if (uuid.equals(menuOrder.uuid)) {
                return i;
            }
        }

        return -1;
    }

    public int getItemCount() {
        return orderList.size();
    }
}

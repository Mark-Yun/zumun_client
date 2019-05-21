/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model.entity.order;

import com.mark.zumo.client.core.database.entity.MenuOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 6. 9.
 */
public class OrderBucket {

    private static final String TAG = "OrderBucket";
    private final Collection<ObservableEmitter<OrderBucket>> emitterCollection;
    private List<MenuOrder> orderList;

    public OrderBucket() {
        orderList = new ArrayList<>();
        emitterCollection = new ArrayList<>();
    }

    public void addOrder(MenuOrder menuOrder) {
        orderList.add(menuOrder);
        notifyOnNext();
    }

    public void setOrder(List<MenuOrder> menuOrderList) {
        orderList = new ArrayList<>(menuOrderList);
        notifyOnNext();
    }

    public void addOrderList(List<MenuOrder> menuOrderList) {
        orderList.addAll(menuOrderList);
        notifyOnNext();
    }

    public OrderBucket addEmitter(ObservableEmitter<OrderBucket> emitter) {
        emitterCollection.add(emitter);
        return this;
    }

    public void notifyOnNext() {
        Collections.sort(orderList, (o1, o2) -> (int) (o2.createdDate - o1.createdDate));
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

    public MenuOrder getOrder(String uuid) {
        MenuOrder remove = orderList.get(getMenuOrderPosition(uuid));
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

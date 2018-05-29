package com.mark.zumo.client.customer.model.entity;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mark on 18. 5. 24.
 */
public class CartItem {
    public final String storeUuid;
    public final String menuUuid;

    private int amount;

    private Collection<OrderDetail> orderDetailList;

    private CartItem(final String storeUuid, final String menuUuid,
                     final Collection<OrderDetail> orderDetailList, final int amount) {
        this.menuUuid = menuUuid;
        this.storeUuid = storeUuid;
        this.orderDetailList = orderDetailList;
        this.amount = amount;
    }

    private CartItem(final String storeUuid, final String menuUuid, final int amount) {
        this(storeUuid, menuUuid, new ArrayList<>(), amount);
    }

    public static CartItem fromMenu(Menu menu) {
        return new CartItem(menu.storeUuid, menu.uuid, 1);
    }

    public static CartItem fromOptionMenu(String storeUuid, final String menuUuid,
                                          Collection<OrderDetail> orderDetailList, final int amount) {
        return new CartItem(storeUuid, menuUuid, orderDetailList, amount);
    }

    public static CartItem empty() {
        return new CartItem(null, null, 0);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public CartItem add(OrderDetail orderDetail) {
        orderDetailList.add(orderDetail);
        return this;
    }

    public CartItem add(Collection<OrderDetail> orderDetails) {
        orderDetailList.addAll(orderDetails);
        return this;
    }

    public CartItem remove(int position) {
        orderDetailList.remove(position);
        return this;
    }

    public Collection<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, CartItem.class);
    }
}

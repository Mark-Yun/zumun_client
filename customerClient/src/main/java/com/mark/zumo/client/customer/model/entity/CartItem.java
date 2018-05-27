package com.mark.zumo.client.customer.model.entity;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.OrderDetail;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mark on 18. 5. 24.
 */
public class CartItem {
    public final String menuUuid;

    private Collection<OrderDetail> orderDetailList;

    private CartItem(final String menuUuid) {
        this.menuUuid = menuUuid;
        orderDetailList = new ArrayList<>();
    }

    private CartItem(final String menuUuid, final Collection<OrderDetail> orderDetailList) {
        this.menuUuid = menuUuid;
        this.orderDetailList = orderDetailList;
    }

    public static CartItem fromMenu(Menu menu) {
        return new CartItem(menu.uuid);
    }

    public static CartItem fromOptionMenu(String menuUuid, Collection<OrderDetail> orderDetailList) {
        return new CartItem(menuUuid, orderDetailList);
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
}

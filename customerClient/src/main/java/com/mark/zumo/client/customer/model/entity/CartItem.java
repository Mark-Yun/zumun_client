package com.mark.zumo.client.customer.model.entity;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.OrderDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mark on 18. 5. 24.
 */
public class CartItem {
    public final String menuUuid;

    private List<OrderDetail> orderDetailList;

    private CartItem(final String menuUuid) {
        this.menuUuid = menuUuid;
        orderDetailList = new ArrayList<>();
    }

    public static CartItem fromMenu(Menu menu) {
        return new CartItem(menu.uuid);
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

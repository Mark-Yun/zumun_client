package com.mark.zumo.client.customer.model.entity;

import com.mark.zumo.client.core.entity.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {
    private List<Menu> menuList;

    public Cart() {
        menuList = new ArrayList<>();
    }

    public void addMenu(Menu menu) {
        menuList.add(menu);
    }

    public void removeMenu(int position) {
        menuList.remove(position);
    }

    public int getCartCount() {
        return menuList.size();
    }
}
